package tmc.eclipse.util;

import fi.helsinki.cs.tmc.core.old.domain.Exercise;
import fi.helsinki.cs.tmc.core.old.utils.ProjectIconHandler;

public class EclipseProjectIconHandler implements ProjectIconHandler{

    @Override
    public void updateIcon(Exercise e) {
        ProjectNatureHelper.updateTMCProjectNature(e);
    }

}
