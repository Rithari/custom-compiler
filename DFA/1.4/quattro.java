// Esercizio 1.4
import java.util.ArrayList;

public class quattro {

  public static boolean scan(String s) {
    int state = 0;
    int i = 0;

    // ArrayList of uppercase characters from A to K
    ArrayList<Character> corsoA = new ArrayList<Character>();
    for (char c = 'A'; c <= 'K'; c++) {
      corsoA.add(c);
    }
    // ArrayList of lowercase characters from a to k
    ArrayList<Character> corsoA_lower = new ArrayList<Character>();
    for (char c = 'a'; c <= 'k'; c++) {
      corsoA_lower.add(c);
    }
    // ArrayList of uppercase characters from L to Z
    ArrayList<Character> corsoB = new ArrayList<Character>();
    for (char c = 'L'; c <= 'Z'; c++) {
      corsoB.add(c);
    }
    // ArrayList of lowercase characters from l to z
    ArrayList<Character> corsoB_lower = new ArrayList<Character>();
    for (char c = 'l'; c <= 'z'; c++) {
      corsoB_lower.add(c);
    }
    // Arraylist of even digits
    ArrayList<Character> evens = new ArrayList<Character>();
    for (char c = '0'; c <= '8'; c+=2) {
      evens.add(c);
    }
    // Arraylist of odd digits
    ArrayList<Character> odds = new ArrayList<Character>();
    for (char c = '1'; c <= '9'; c+=2) {
      odds.add(c);
    }


    while (state >= 0 && i < s.length()) {
      final char ch = s.charAt(i++);

      switch (state) {
        case 0:
          if (ch == ' ') state = 0;
          else if (evens.contains(ch)) state = 1;
          else if (odds.contains(ch)) state = 2;
          else state = -1;
          break;
        case 1:
          if (ch == ' ') state = 5;
          else if (evens.contains(ch)) state = 1;
          else if (odds.contains(ch)) state = 2;
          else if (corsoA.contains(ch)) state = 3;
          else state = -1;
          break;
        case 2:
          if (ch == ' ') state = 6;
          else if (evens.contains(ch)) state = 1;
          else if (odds.contains(ch)) state = 2;
          else if (corsoB.contains(ch)) state = 3;
          else state = -1;
          break;
        case 3:
          if (ch == ' ') state = 4;
          else if (corsoA_lower.contains(ch) || corsoB_lower.contains(ch)) state = 3;
          else state = -1;
          break;
        case 4:
          if (ch == ' ') state = 4;
          else if (corsoA.contains(ch) || corsoB.contains(ch)) state = 3;
          else state = -1;
          break;
        case 5:
          if (ch == ' ') state = 5;
          else if (corsoA.contains(ch)) state = 3;
          break;
        case 6:
          if (ch == ' ') state = 6;
          else if (corsoB.contains(ch)) state = 3;
          break;
      }
    }
    return state == 3 || state == 4;
  }

  public static void main(String[] args) {
	if(args.length == 0) System.out.println("Nessun argomento in input fornito.");
	else System.out.println(scan(args[0]) ? "OK" : "NOPE");
  }
}
