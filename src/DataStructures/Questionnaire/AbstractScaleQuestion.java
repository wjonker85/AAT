package DataStructures.Questionnaire;

/**
 * Created by marcel on 3/25/14.
 */
public abstract class AbstractScaleQuestion extends AbstractQuestion {

    private String left;
    private String right;
    private int size;

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
