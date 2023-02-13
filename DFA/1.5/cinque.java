// Esercizio 1.5
import java.util.ArrayList;

public class cinque {

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
          if (corsoA.contains(ch)) state = 1;
          else if(corsoB.contains(ch)) state = 2;
          else state = -1;
          break;
        case 1:
          if (corsoA_lower.contains(ch) || corsoB_lower.contains(ch)) state = 1;
          else if (evens.contains(ch)) state = 3;
          else if (odds.contains(ch)) state = 4;
          else state = -1;
          break;
        case 2:
          if (corsoB_lower.contains(ch) || corsoA_lower.contains(ch)) state = 2;
          else if (evens.contains(ch)) state = 5;
          else if (odds.contains(ch)) state = 6;
          else state = -1;
          break;
        case 3:
          if (evens.contains(ch)) state = 3;
          else if (odds.contains(ch)) state = 4;
          else state = -1;
          break;
        case 4:
          if (odds.contains(ch)) state = 4;
          else if (evens.contains(ch)) state = 3;
          else state = -1;
          break;
        case 5:
          if(evens.contains(ch)) state = 5;
          else if (odds.contains(ch)) state = 6;
          else state = -1;
          break;
        case 6:
          if(odds.contains(ch)) state = 6;
          else if (evens.contains(ch)) state = 5;
          else state = -1;
          break;
      }
    }
    return state == 3 || state == 6;
  }

  public static void main(String[] args) {
	if(args.length == 0) System.out.println("Nessun argomento in input fornito.");
	else System.out.println(scan(args[0]) ? "OK" : "NOPE");
  }
}
