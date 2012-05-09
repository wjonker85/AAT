package AAT.Util;

import javax.swing.*;
import java.awt.*;

/**
 * The TitledSeparator generates a JSeparator with a text (title) at a
 * specified position (pos).
 * The code was derived from an example from Volker B�hm (volker@vboehm.de)
 * in a posting in the de.comp.lang.java newsgroup on 2003-03-13.
 * <p/>
 * pos can take the following values:
 * <code>
 * < -1    the text is aligned to the right, followed by a abs(pos)
 * long separator
 * <p/>
 * = -1    the text is right aligned
 * <p/>
 * = 0     the text is centered with separators on both sides
 * <p/>
 * = 1     the text is left aligned
 * <p/>
 * > 1     the text is left aligned after a abs(pos) long separator
 * <p/>
 * The unit of pos is pixel.
 * <p/>
 * Examples:
 * TitledSeparator("Hallo",0)    ------------- Hallo -------------
 * <p/>
 * TitledSeparator("Hallo",1)    Hallo ---------------------------
 * <p/>
 * TitledSeparator("Hallo",-1)   --------------------------- Hallo
 * <p/>
 * TitledSeparator("Hallo")      ---- Hallo ----------------------
 * TitledSeparator("Hallo",4)    ---- Hallo ----------------------
 * <p/>
 * TitledSeparator("Hallo",-5)   --------------------- Hallo -----
 * </code>
 */
public class TitledSeparator extends JPanel {
    // the label for the title
    private JLabel mTitleLabel;

    // the default position if no one is supplied
    private static final int DEFAULT_POS = 7;

    /**
     * Create a new title separator with a empty title.
     */
    public TitledSeparator() {
        this("", DEFAULT_POS);
    }

    /**
     * Create a new title separator with the handed title.
     *
     * @param pTitle the title text
     */
    public TitledSeparator(String pTitle) {
        this(pTitle, DEFAULT_POS);
    }

    /**
     * Create a new title separator with the handed title and position.
     *
     * @param pTitle the title text
     * @param pPos   the offest of pTitle
     */
    public TitledSeparator(String pTitle, int pPos) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (pPos != 1) {
            JSeparator sep = new JSeparator();
            if (pPos > 0) {
                sep.setPreferredSize(
                        new Dimension(pPos, sep.getPreferredSize().height));
                gbc.weightx = 0;
            } else {
                gbc.weightx = 1;
            }
            gbc.insets = new Insets(2, 0, 0, 3);
            add(sep, gbc);
        }
        mTitleLabel = new JLabel(pTitle);
        Font f = UIManager.getFont("TitledBorder.font");
        Color c = UIManager.getColor("TitledBorder.titleColor");
        mTitleLabel.setFont(f);
        mTitleLabel.setForeground(c);
        add(mTitleLabel);


        if (pPos != -1) {
            JSeparator sep = new JSeparator();
            if (pPos < 0) {
                sep.setPreferredSize(
                        new Dimension(-pPos, sep.getPreferredSize().height));
                gbc.weightx = 0;
            } else {
                gbc.weightx = 1;
            }
            gbc.insets = new Insets(2, 3, 0, 0);
            add(sep, gbc);
        }
    }

    /**
     * Return the text of the title.
     *
     * @return the title we display
     */
    public String getText() {
        return mTitleLabel.getText();
    }

    /**
     * Set the text of the title.
     *
     * @param pTitle the title we should display
     */
    public void setText(String pTitle) {
        mTitleLabel.setText(pTitle);
    }
}
