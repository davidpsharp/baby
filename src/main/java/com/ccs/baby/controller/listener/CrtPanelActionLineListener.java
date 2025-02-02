package com.ccs.baby.controller.listener;

import com.ccs.baby.core.Control;
import com.ccs.baby.ui.CrtPanel;

public class CrtPanelActionLineListener {

    private final Control control;
    private final CrtPanel crtPanel;

    public CrtPanelActionLineListener(Control control, CrtPanel crtPanel) {
        this.crtPanel = crtPanel;
        this.control = control;
    }

    /**
     * Handles changes to the action line state when notified by `StaticisorPanelController`.
     * <p>
     * This method is triggered whenever the manual/automatic mode or selected action line switches change.
     * Depending on the current mode, it updates the CRT panel accordingly:
     * </p>
     * <ul>
     *     <li><strong>Automatic Mode (`isAutoMode = true`):</strong> The action line is determined by the control instruction.</li>
     *     <li><strong>Manual Mode (`isAutoMode = false`):</strong> The action line is set based on the selected line switches.</li>
     * </ul>
     * <p>
     * The CRT panel is updated to reflect the new action line, ensuring accurate visualisation.
     * </p>
     *
     * @param isAutoMode {@code true} if in automatic mode (action line set by control instruction),
     *                   {@code false} if in manual mode (action line set by user-selected switches).
     * @param lineValue The selected action line value when in manual mode.
     */
    public void onActionLineChange(boolean isAutoMode, int lineValue) {
        if (isAutoMode) {
            crtPanel.setActionLine(Control.getLineNumber(control.getControlInstruction()));
        } else {
            crtPanel.setActionLine(lineValue);
        }
    }

}