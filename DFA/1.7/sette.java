// Esercizio 1.7

public class sette {

  public static boolean scan(String s) {
    int state = 0;
    int i = 0;

    // Name being tested = Leo (Assumes first capital letter)
    // L = {L, e, o}
    // Does not work with dynamic names, obviously.
    // Also doesn't consider the lenght of the character

    while (state >= 0 && i < s.length()) {
      final char ch = s.charAt(i++);

      switch (state) {
        case 0:
          if (ch == 'L') state = 1;
          else if (ch != 'L') state = 4;
          else state = -1;
          break;
        case 1:
          if (ch == 'e') state = 2;
          else if(ch != 'e') state = 5;
          else state = -1;
          break;
        case 2:
          if (ch >= 'a' && ch <= 'z') state = 3;
          else state = -1;
          break;
        case 4:
          if (ch  == 'e') state = 5;
          else state = -1;
          break;
        case 5:
          if(ch == 'o') state = 3;
          else state = -1;
          break;
      }
    }
    return state == 3;
  }

  public static void main(String[] args) {
	if(args.length == 0) System.out.println("Nessun argomento in input fornito.");
	else System.out.println(scan(args[0]) ? "OK" : "NOPE");
  }
}
