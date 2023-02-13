// Esercizio 1.2
import java.util.ArrayList;

public class tre {

  public static boolean scan(String s) {
    int state = 0;
    int i = 0;

    // Arraylists for the character range of the A and B course
    ArrayList<Character> corsoA = new ArrayList<Character>();
    for (char c = 'a'; c <= 'k'; c++) {
      corsoA.add(c);
    }
    for (char c = 'A'; c <= 'K'; c++) {
      corsoA.add(c);
    }
    ArrayList<Character> corsoB = new ArrayList<Character>();
    for (char c = 'l'; c <= 'z'; c++) {
      corsoA.add(c);
    }
    for (char c = 'L'; c <= 'Z'; c++) {
      corsoA.add(c);
    }
    // Arraylist of digits
    ArrayList<Character> digits = new ArrayList<Character>();
    for (char c = '0'; c <= '9'; c++) {
      digits.add(c);
    }


    while (state >= 0 && i < s.length()) {
      final char ch = s.charAt(i++);

      switch (state) {

        case 0:
          if (digits.contains(ch) && ((ch-'0') % 2) == 0) { 
            state = 1;
          }
          else if (digits.contains(ch) && ((ch-'0') % 2) != 0) {
            state = 2;
          } else state = -1;
          break;
        case 1:
          if (digits.contains(ch) && ((ch-'0') % 2) == 0) state = 1; else if (digits.contains(ch) && ((ch-'0') % 2) != 0) state = 2; else if (corsoA.contains(ch)) { state = 3; System.out.println("corso a vado caso 3");} else state =
            -1;
          break;
        case 2:
          if (digits.contains(ch) && ((ch-'0') % 2) == 0) state = 1; else if (digits.contains(ch) && ((ch-'0') % 2) != 0) state = 2; else if (corsoB.contains(ch)) { state = 4; System.out.println("Corso b vado caso 4");} else state =
            -1;
          break;
        case 3:
          if(digits.contains(ch)) state =-1; else state = 3;
          break;
        case 4:
          if(digits.contains(ch)) state = -1; else state = 4;
          break;
      }
    }
    if(state == 3) System.out.print("T2 : ");
    else if(state == 4) System.out.print("T3 : ");
    return state == 3 || state == 4;
  }

  public static void main(String[] args) {
	if(args.length == 0) System.out.println("Nessun argomento in input fornito.");
	else System.out.println(scan(args[0]) ? "OK" : "NOPE");
  }
}