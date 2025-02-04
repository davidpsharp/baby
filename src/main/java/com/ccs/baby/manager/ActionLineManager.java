package com.ccs.baby.manager;

import com.ccs.baby.ui.CrtPanel;
import com.ccs.baby.ui.StaticisorPanel;
import com.ccs.baby.core.Control;

public class ActionLineManager {
    private final StaticisorPanel staticisorPanel;
    private final CrtPanel crtPanel;
    private final Control control;

    public ActionLineManager(StaticisorPanel staticisorPanel, CrtPanel crtPanel, Control control) {
        this.staticisorPanel = staticisorPanel;
        this.crtPanel = crtPanel;
        this.control = control;
    }

    /**
     * Updates the action line in the CRT panel based on the mode set in the Staticisor panel.
     * <ul>
     *   <li>In "automatic" mode (TRUE), the action line is set using the control's instruction.</li>
     *   <li>In "manual" mode (FALSE), it uses the value from the line switches.</li>
     * </ul>
     */
    public void updateActionLine() {
        if (staticisorPanel.getManAuto()) {
            crtPanel.setActionLine(Control.getLineNumber(control.getControlInstruction()));
        } else {
            crtPanel.setActionLine(staticisorPanel.getLineValue());
        }
    }
}