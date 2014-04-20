package co.arcs.android.fileselector.demo;

import co.arcs.android.fileselector.FileSelectorActivity;

/**
 * Empty {@link FileSelectorActivity} subclasses so we can have multiple copies
 * with various themes declared in the manifest.
 */
public class ThemedActivities {

    public static class Light extends FileSelectorActivity {
    }

    public static class Dark extends FileSelectorActivity {
    }

    public static class LightDialog extends FileSelectorActivity {
    }

    public static class DarkDialog extends FileSelectorActivity {
    }
}
