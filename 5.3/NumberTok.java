public class NumberTok extends Token {
    int num;
    public NumberTok(String num) {
        super(Tag.NUM);
        this.num = Integer.parseInt(num);
    }

    public String toString() {return "<" + tag + "," + num + ">";}

}