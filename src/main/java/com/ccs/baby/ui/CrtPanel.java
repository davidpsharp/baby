package com.ccs.baby.ui;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Image;

import com.ccs.baby.core.Store;
import com.ccs.baby.manager.AnimationManager;
import com.ccs.baby.core.Control;
import com.ccs.baby.utils.ImageUtils;

public class CrtPanel extends JPanel {

    public enum DisplayType {
        STORE,
        CONTROL,
        ACCUMULATOR
    }

    // NOTE: This was 291x291 for raster area before circular border
    public static final int DEFAULT_CRT_WIDTH = 400;
    public static final int DEFAULT_CRT_HEIGHT = 290;

    public static final int DEFAULT_RASTER_OFFSET_X = 200; // Number of pixels vertically from left
    public static final int DEFAULT_RASTER_OFFSET_Y = 95; // Number of pixels horizontally from top

    public static final int DEFAULT_BIT_LENGTH = 6; // The length of a bit as appears on the crt
    public static final int DEFAULT_SPACING = 3;
    private static final int DEFAULT_SCALE = 2400;

    // NOTE: Cached images are all 7x3 GIFs
    private static final Image BIT_ONE_IMAGE = ImageUtils.loadImage("/images/bit1.gif", DEFAULT_SCALE);
    private static final Image BIT_ZERO_IMAGE = ImageUtils.loadImage("/images/bit0.gif", DEFAULT_SCALE);
    private static final Image BIT_ONE_BRIGHT_IMAGE = ImageUtils.loadImage("/images/bit1bright.gif", DEFAULT_SCALE);
    private static final Image BIT_ZERO_BRIGHT_IMAGE = ImageUtils.loadImage("/images/bit0bright.gif", DEFAULT_SCALE);

    private DisplayType currentDisplay = DisplayType.STORE; // The current display type
    private final boolean renderAccurately = true; // Whether to render the Control and Accumulator accurately
    private final BufferedImage bufferedImage = new BufferedImage(DEFAULT_CRT_WIDTH, DEFAULT_CRT_HEIGHT, BufferedImage.TYPE_INT_ARGB); // The image to be drawn
    private final Graphics2D big = bufferedImage.createGraphics();
    private int actionLine = 0;

    private final Store store;
    private final Control control;

    /**
     * Creates a new instance of CrtPanel.
     *
     * @param store   the store to be displayed
     * @param control the control to be displayed
     */
    public CrtPanel(Store store, Control control) {
        this.store = store;
        this.control = control;

        render();
        repaint();
    }

    /**
     * Change the display type of the CRT.
     *
     * @param display the new display type
     */
    public void setCrtDisplay(DisplayType display) {
        DisplayType beforeDisplay = currentDisplay;

        for (int x = 0; x < 32; x++)
            store.isLineAltered[x] = true;

        switch (display) {
            case STORE:
            case ACCUMULATOR:
            case CONTROL:
                currentDisplay = display;
                break;
            default:
                currentDisplay = DisplayType.STORE;
                break;
        }

        // if changed display then redraw
        if (currentDisplay != beforeDisplay) {

            // set so all lines need redrawing
            for (int lineNumber = 0; lineNumber < 32; lineNumber++)
                store.isLineAltered[lineNumber] = true;

            render();
            repaint();
        }
    }

    /**
     * <p>Paints the CRT panel by drawing the pre-rendered buffered image.</p>
     * <p>Calls {@code super.paintComponent} to clear the panel, then draws the image at (0,0).</p>
     *
     * @param graphicsIn the graphics context used for painting
     */
    @Override
    public void paintComponent(Graphics graphicsIn) {
        super.paintComponent(graphicsIn);

        Graphics2D graphics2D = (Graphics2D) graphicsIn;
        graphics2D.drawImage(bufferedImage, 0, 0, this);
    }

    /**
     * Repaints the amount of screen necessary.
     */
    public synchronized void efficientRepaint() {
        switch (currentDisplay) {
            // only redraw the store lines that have changed
            case STORE:
                for (int lineNumber = 0; lineNumber < 31; lineNumber++) {
                    if (store.isLineAltered[lineNumber]) {
                        repaint(0, DEFAULT_SPACING + lineNumber * DEFAULT_BIT_LENGTH, DEFAULT_CRT_WIDTH, DEFAULT_SPACING + ((lineNumber + 1) * DEFAULT_BIT_LENGTH));
                        store.isLineAltered[lineNumber] = false;
                    }
                }
                break;
            // redraw top line only
            case ACCUMULATOR:
                if (renderAccurately) repaint();
                else repaint(0, 0, DEFAULT_CRT_WIDTH, 3 + DEFAULT_BIT_LENGTH);
                break;
            // redraw top 2 lines only
            case CONTROL:
                if (renderAccurately) repaint();
                else repaint(0, 0, DEFAULT_CRT_WIDTH, 3 + DEFAULT_BIT_LENGTH + DEFAULT_BIT_LENGTH);
                break;
            // redraw entire display
            default:
                repaint();
                break;
        }
    }

    public void setActionLine(int newActionLine) {
        // negative values turn off the action line
        if (newActionLine < 0) {
            // wipe out old action line
            if (actionLine > 0 && actionLine < 32)
                store.isLineAltered[actionLine] = true;
            actionLine = newActionLine;
        } else {
            if (actionLine != newActionLine) {
                // old actionLine could be negative if turned off
                if (actionLine > 0 && actionLine < 32)
                    store.isLineAltered[actionLine] = true;
                // store new action line
                actionLine = newActionLine;
                // set to draw new action line
                store.isLineAltered[actionLine] = true;
                render();
                repaint();
            }
        }
    }

    // render the graphics to the buffered image
    private synchronized void render() {
        switch (currentDisplay) {
            case STORE:
                renderStore();
                break;
            case ACCUMULATOR:
                renderAccumulator();
                break;
            case CONTROL:
                renderControl();
                break;
            default:
                renderStore();
                break;
        }
    }

    // renders the Store to the buffered image
    public void renderStore() {

        for (int lineNumber = 0; lineNumber < 32; lineNumber++) {
            // for each line that has been changed since last redraw
            if (store.isLineAltered[lineNumber]) {
                // if redrawing the action line then draw it brighter
                if (actionLine == lineNumber) {
                    drawActionDataAtLine(store.getLine(lineNumber), lineNumber, big);
                }
                // otherwise draw a normal line
                else {
                    drawDataAtLine(store.getLine(lineNumber), lineNumber, big);
                }
            }
        }
    }

    // render control
    public void renderControl() {
        // if rendering accurately repeat display all the way down
        if (renderAccurately) {
            // if the baby is running then PI exists so draw it
            if (AnimationManager.animationRunning) {
                // if baby is running then CI and PI are displayed
                for (int line = 0; line < 32; line += 2) {
                    drawDataAtLine(control.getControlInstruction(), line, big);
                    drawDataAtLine(control.getPresentInstruction(), line + 1, big);
                }
            } else {
                // when not running PI doesn't exist so only display CI
                for (int line = 0; line < 32; line++) {
                    drawDataAtLine(control.getControlInstruction(), line, big);
                }
            }
        } else {
            // otherwise display both CI and PI but only in top two lines
            drawDataAtLine(control.getControlInstruction(), 0, big);
            drawDataAtLine(control.getPresentInstruction(), 1, big);
        }
    }

    // render accumulator
    public void renderAccumulator() {
        drawDataAtLine(control.getAccumulator(), 0, big);

        // if rendering accuraterly draw accumulator on all lines
        if (renderAccurately) {
            for (int line = 1; line < 32; line++)
                drawDataAtLine(control.getAccumulator(), line, big);
        }
    }

    // draw the given data for a line at the specified position on the specified graphics context
    private void drawDataAtLine(int data, int line, Graphics2D graphicsContext) {
        for (int bitNumber = 31; bitNumber >= 0; bitNumber--) {
            // if bit set then
            if (((data >> bitNumber) & 1) == 1) {
                graphicsContext.drawImage(BIT_ONE_IMAGE, DEFAULT_RASTER_OFFSET_X + DEFAULT_SPACING + bitNumber * DEFAULT_BIT_LENGTH, DEFAULT_RASTER_OFFSET_Y + DEFAULT_SPACING + line * DEFAULT_BIT_LENGTH, null);
            } else {
                graphicsContext.drawImage(BIT_ZERO_IMAGE, DEFAULT_RASTER_OFFSET_X + DEFAULT_SPACING + bitNumber * DEFAULT_BIT_LENGTH, DEFAULT_RASTER_OFFSET_Y + DEFAULT_SPACING + line * DEFAULT_BIT_LENGTH, null);
            }
        }
    }

    // same as drawDataAtLine() but for the brighter action line
    private void drawActionDataAtLine(int data, int line, Graphics2D graphicsContext) {
        for (int bitNumber = 31; bitNumber >= 0; bitNumber--) {
            // if bit set then
            if (((data >> bitNumber) & 1) == 1) {
                graphicsContext.drawImage(BIT_ONE_BRIGHT_IMAGE, DEFAULT_RASTER_OFFSET_X + DEFAULT_SPACING + bitNumber * DEFAULT_BIT_LENGTH, DEFAULT_RASTER_OFFSET_Y + DEFAULT_SPACING + line * DEFAULT_BIT_LENGTH, null);
            } else {
                graphicsContext.drawImage(BIT_ZERO_BRIGHT_IMAGE, DEFAULT_RASTER_OFFSET_X + DEFAULT_SPACING + bitNumber * DEFAULT_BIT_LENGTH, DEFAULT_RASTER_OFFSET_Y + DEFAULT_SPACING + line * DEFAULT_BIT_LENGTH, null);
            }
        }
    }


    public DisplayType getCurrentDisplay() {
        return currentDisplay;
    }

}