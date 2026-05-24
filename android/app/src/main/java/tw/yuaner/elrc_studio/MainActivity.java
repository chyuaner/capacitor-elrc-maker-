package tw.yuaner.elrc_studio;

import android.os.Bundle;
import androidx.core.view.WindowCompat;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(ThemeControlPlugin.class);
        super.onCreate(savedInstanceState);
        
        // Force edge-to-edge and log it
        android.util.Log.d("CapacitorApp", "Setting Edge-to-Edge mode");
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
}
