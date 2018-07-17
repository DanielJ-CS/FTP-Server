/**
 * Created by Daniel on 2017-02-04.
 */



// We convert from ASCII characters to string in this conversion.
// To check where what we are converting we can look at a ASCII table:
//         http://ee.hawaii.edu/~tep/EE160/Book/chap4/subsection2.1.1.1.html
// Looking at the table gives us the values we need for keyboard characters
//
public class BytesToString {

    public BytesToString() {}

    // Looking at the table in the comments at the top of the page,
    // we should include these characters and these characters only
    public static boolean keyboardAsciiText(byte text) {

        if (text >= 32 && text < 127) {
            return true;
        } else {
            return false;
        }
    }

    // Standard StringBuilder method that can be found on stack overflow or any google search
    // Takes bytes and turns it into String and appends it to a stringbuilder
    public static String readableText(byte[] buffer, int offset, int size) {

        StringBuilder strBuild = new StringBuilder();

        for (int i = 0; i < size; i++) {
            byte b = buffer[offset + i];
            if ( keyboardAsciiText(b))
                strBuild.append((char) b);
        }
        return strBuild.toString();
    }
}

