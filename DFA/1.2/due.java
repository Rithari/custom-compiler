// Esercizio 1.2
import java.util.ArrayList;

public class due {

  public static boolean scan(String s) {
    int state = 0;
    int i = 0;
    // Arraylist of the alphabet uppercase and lowercase
    ArrayList<Character> alphabet = new ArrayList<Character>();
    for (char c = 'a'; c <= 'z'; c++) {
      alphabet.add(c);
    }
    for (char c = 'A'; c <= 'Z'; c++) {
      alphabet.add(c);
    }
    // Arraylist of the digits
    ArrayList<Character> digits = new ArrayList<Character>();
    for (char c = '0'; c <= '9'; c++) {
      digits.add(c);
    }


    while (state >= 0 && i < s.length()) {
      final char ch = s.charAt(i++);

      switch (state) {
        case 0:
          if (alphabet.contains(ch)) state = 2; else if (ch == '-') state = 1; else state =
            -1;
          break;
        case 1:
          if (ch == '_') state = 1; else if (alphabet.contains(ch) || digits.contains(ch)) state = 2; else state =
            -1;
          break;
        case 2:
          if (alphabet.contains(ch) || digits.contains(ch) || ch == '_') state = 2; else state =
            -1;
          break;
      }
    }
    return state == 2;
  }

  public static void main(String[] args) {
	if(args.length == 0) System.out.println("Nessun argomento in input fornito.");
	else System.out.println(scan(args[0]) ? "OK" : "NOPE");
  }
}