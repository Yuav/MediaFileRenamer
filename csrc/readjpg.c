#include <stdio.h>
#include <stdlib.h>

#define SWAP_ENDIAN_2(x) ( (((x) & 0x00FF) << 8) | (((x) & 0xFF00) >> 8) )

#define SWAP_ENDIAN_4(x) ( (((x) & 0x000000FF) << 24) | (((x) & 0x0000FF00) << 8) | (((x) & 0x00FF0000) >> 8) | (((x) & 0xFF000000) >> 24) )

void read_chunk(FILE *fp);
void read_E1_header(FILE *fp, unsigned short size);
void read_other_chunk(FILE *fp, unsigned short size);
unsigned long read_ifd(FILE *fp, unsigned long offset);
char * map_tag(unsigned short tag);

unsigned long tiff_offset;

int main(int argc, char **argv)
{
    FILE *fp;
    const char *jpgfile = argv[1];
    fp = fopen(jpgfile, "rb");
    if (fp == NULL) {
        fprintf(stderr, "Error reading %s\n", jpgfile);
        return EXIT_FAILURE;
    }
    read_chunk(fp);
    read_chunk(fp);
    read_chunk(fp);
    fclose(fp);
    return EXIT_SUCCESS;
}

void read_chunk(FILE *fp)
{
    unsigned short marker;
    fread(&marker, sizeof(marker), 1, fp);
    marker = SWAP_ENDIAN_2(marker);
    printf("Marker 0x%X\n", marker);
    if (marker != 0xFFD8) {
        unsigned short size;
        fread(&size, sizeof(size), 1, fp);
        size = SWAP_ENDIAN_2(size);
        size -= 2;
        printf("  Size = %d\n", size);
        if (marker == 0xFFE1) {
            read_E1_header(fp, size);
        } else {
            read_other_chunk(fp, size);
        }
    }
}

void read_E1_header(FILE *fp, unsigned short size)
{
    printf("  E1 Header\n");

    /*
     * Exif Header
     */
    char exif_hdr[6];
    fread(exif_hdr, sizeof(exif_hdr), 1, fp);
    printf("    %c\n", exif_hdr[0]);

    /*
     * TIFF Header
     */
    tiff_offset = ftell(fp);
    unsigned char tiff_hdr[8];
    fread(tiff_hdr, sizeof(tiff_hdr), 1, fp);
    if (tiff_hdr[0] == 'M') {
        printf("    Motorola align\n");
    } else if (tiff_hdr[0] == 'I') {
        printf("    Intel align\n");
    } else {
        printf("    UNKNOWN align\n");
    }
    unsigned int ifd_offset = (tiff_hdr[4]<<24)
        | (tiff_hdr[5]<<16)
        | (tiff_hdr[6]<<8)
        | (tiff_hdr[7]);
    printf("    IFD Offset = %d\n", ifd_offset);

    /*
     * IFD
     */
    while (ifd_offset != 0) {
      ifd_offset = read_ifd(fp, ifd_offset + tiff_offset);
    }
}

unsigned long read_ifd(FILE *fp, unsigned long offset)
{
    fseek(fp, offset, SEEK_SET);
    unsigned long ifd_start_offset = ftell(fp);
    unsigned short ifd_entries;
    fread(&ifd_entries, sizeof(ifd_entries), 1, fp);
    ifd_entries = SWAP_ENDIAN_2(ifd_entries);
    printf("    IFD Entries = %d\n", ifd_entries);
    unsigned long exif_sub_ifd;
    unsigned short e;
    for(e = 0; e < ifd_entries; e++) {
        unsigned short tag;
        fread(&tag, sizeof(tag), 1, fp);
        tag = SWAP_ENDIAN_2(tag);

        unsigned short format;
        fread(&format, sizeof(format), 1, fp);
        format = SWAP_ENDIAN_2(format);
        unsigned short bpc;
        switch(format) {
            case 1:
            case 2:
            case 6:
            case 7:
                bpc = 1;
                break;
            case 3:
            case 8:
                bpc = 2;
                break;
            case 4:
            case 9:
            case 11:
                bpc = 4;
                break;
            case 5:
            case 10:
            case 12:
                bpc = 8;
                break;
        }

        unsigned int comps;
        fread(&comps, sizeof(comps), 1, fp);
        comps = SWAP_ENDIAN_4(comps);
        unsigned long total_data_length = (unsigned long)(bpc*comps);

        unsigned int data;
        fread(&data, sizeof(data), 1, fp);
        data = SWAP_ENDIAN_4(data);
        char * f3v;
        unsigned int num, den;
        double fav;
        if (total_data_length > 4) {
            unsigned long o = ftell(fp);
            fseek(fp, tiff_offset + data, SEEK_SET);
            if (format == 2) {
                unsigned int f3vlen = total_data_length * sizeof(char);
                f3v = malloc(f3vlen);
                fread(f3v, f3vlen, 1, fp);
                //printf("....%s\n", d);
            } else if (format == 5) {
                fread(&num, sizeof(num), 1, fp);
                fread(&den, sizeof(den), 1, fp);
                num = SWAP_ENDIAN_4(num);
                den = SWAP_ENDIAN_4(den);
                //printf("....%d/%d\n", num, den);
            } else if (format == 0xA) {
                fav = (double)data;
            }
            fseek(fp, o, SEEK_SET);
        } else {
            if (format == 3) {
                if (comps == 1) {
                    data = (data & 0xFFFF0000) >> 16;
                }
            } else if (format == 4) {
                if (comps == 1) {
                    //data = (data & 0xFFFF0000) >> 16;
                }
            }
        }

        printf("      %d : Tag %X (%s) : ", e, tag, map_tag(tag));
        if (format == 2) {
            printf("Value %s\n", f3v);
            free(f3v);
        } else if (format == 3) {
            printf("Value %d\n", data);
        } else if (format == 4) {
            printf("Value %d\n", data);
        } else if (format == 5) {
            printf("Value %d/%d\n", num, den);
        } else if (format == 0xA) {
            printf("Value %lf\n", fav);
        } else {
            printf("Format %X : Comps %X : Data %X : Total data length %ld\n", format, comps, data, total_data_length);
        }
        if (tag == 0x8769) {
            exif_sub_ifd = data;
            unsigned long o = ftell(fp);
            read_ifd(fp, data + tiff_offset);
            fseek(fp, o, SEEK_SET);
        }
    }
    unsigned int next_ifd_offset;
    fread(&next_ifd_offset, sizeof(next_ifd_offset), 1, fp);
    next_ifd_offset = SWAP_ENDIAN_4(next_ifd_offset);
    printf("    Next IFD %X\n", next_ifd_offset);
    return next_ifd_offset;
}

void read_other_chunk(FILE *fp, unsigned short size)
{
    fseek(fp, size, SEEK_CUR);
    printf("  Now at 0x%lX\n", ftell(fp));
}

char * map_tag(unsigned short tag)
{
    switch(tag) {
        case 0x0103: return "Compression"; break;
        case 0x010F: return "Make"; break;
        case 0x0110: return "Model"; break;
        case 0x0112: return "Orientation"; break;
        case 0x011A: return "XResolution"; break;
        case 0x011B: return "YResolution"; break;
        case 0x0128: return "ResolutionUnit"; break;
        case 0x0201: return "JpegIFOffset"; break;
        case 0x0202: return "JpegIFByteCount"; break;
        case 0x0213: return "YCbCrPositioning"; break;
        case 0x829A: return "ExposureTime"; break;
        case 0x829D: return "FNumber"; break;
        case 0x8769: return "ExifOffset"; break;
        case 0x8822: return "ExposureProgram"; break;
        case 0x8827: return "ISOSpeedRatings"; break;
        case 0x9000: return "ExifVersion"; break;
        case 0x9003: return "DateTimeOriginal"; break;
        case 0x9004: return "DateTimeDigitized"; break;
        case 0x9101: return "ComponentConfiguration"; break;
        case 0x9201: return "ShutterSpeedValue"; break;
        case 0x9202: return "ApertureValue"; break;
        case 0x9204: return "ExposureBiasValue"; break;
        case 0x9205: return "MaxApertureValue"; break;
        case 0x9207: return "MeteringMode"; break;
        case 0x9208: return "LightSource"; break;
        case 0x9209: return "Flash"; break;
        case 0x920A: return "FocalLength"; break;
        case 0x927C: return "MakerNote "; break;
        case 0xA000: return "FlashPixVersion"; break;
        case 0xA001: return "ColorSpace"; break;
        case 0xA002: return "ExifImageWidth"; break;
        case 0xA003: return "ExifImageHeight"; break;
        case 0xA005: return "ExifInteroperabilityOffset"; break;
        case 0xA215: return "ExposureIndex"; break;
        case 0xA217: return "SensingMethod"; break;
        case 0xA300: return "FileSource"; break;
        case 0xA301: return "SceneType"; break;
        case 0xA401: return "CustomRendered"; break;
        case 0xA402: return "ExposureMode"; break;
        case 0xA403: return "WhiteBalance"; break;
        case 0xA404: return "DigitalZoomRatio"; break;
        case 0xA405: return "FocalLengthIn35mm"; break;
        case 0xA406: return "SceneCaptureType"; break;
        case 0xA407: return "GainControl"; break;
        case 0xA408: return "Contrast"; break;
        case 0xA409: return "Saturation"; break;
        case 0xA40A: return "Sharpness"; break;
        case 0xA40C: return "SubjectDistanceRange"; break;
    }
    return "UNKNOWN";
}
