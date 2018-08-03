/**
 * Created by zn on 2018/7/30.
 */
public class Book<F extends Exception> {
    private F name;
    private F author;
    public F getName() {
        return name;
    }
    public void setName(F name) {
        this.name = name;
    }

    public static void main(String ... args) {
        Book<NullPointerException> book = new Book();
        book.setName(new NullPointerException());
    }

}
