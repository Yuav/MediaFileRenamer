package siahu.media.base;

import java.io.File;
import java.io.IOException;

public interface MediaIdentifier {

	MediaReader getReader(File media) throws IOException;

}
