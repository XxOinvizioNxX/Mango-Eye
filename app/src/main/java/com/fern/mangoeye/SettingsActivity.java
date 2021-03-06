/*
 * Copyright (C) 2021 Fern H., Mango-Eye Android application
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fern.mangoeye;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

import org.opencv.android.CameraBridgeViewBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getName();

    private ArrayList<String> cameraOptions, externalFilesDirs, videoFormats;

    // Local settings
    private String externalFilesDir;
    private int cameraID;
    private boolean enableFlashlight;
    private String videoFormat;
    private int sensitivity;
    private double sizeThreshold;
    private int serverPort;

    // Elements
    private Spinner spinnerStorages, cameraIDSpinner, formatSpinner;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchFlashlight;
    private Slider sensitivitySlider, sizeThresholdSlider;
    private EditText serverPortText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Get list of storages
        File[] files = getBaseContext().getExternalFilesDirs(null);
        externalFilesDirs = new ArrayList<>();
        for (File file : files) externalFilesDirs.add(file.getAbsolutePath());
        Log.i(TAG, "Available storages: " + Arrays.toString(externalFilesDirs.toArray()));

        // Get list of cameras
        cameraOptions = new ArrayList<>(Arrays.asList(
                getResources().getStringArray(R.array.camera_options)));

        // Create list of formats
        videoFormats = new ArrayList<>();
        videoFormats.add("mp4");
        videoFormats.add("mkv");

        // Initialize elements
        spinnerStorages = findViewById(R.id.spinnerStorages);
        cameraIDSpinner = findViewById(R.id.cameraIDSpinner);
        switchFlashlight = findViewById(R.id.switchFlashlight);
        formatSpinner = findViewById(R.id.formatSpinner);
        sensitivitySlider = findViewById(R.id.sensitivitySlider);
        sizeThresholdSlider = findViewById(R.id.sizeThresholdSlider);
        serverPortText = findViewById(R.id.serverPortText);

        // Connect Restore button
        findViewById(R.id.settingsResetBtn).setOnClickListener(view -> {
            // Reset settings to default
            externalFilesDir = externalFilesDirs.get(0);
            cameraID = CameraBridgeViewBase.CAMERA_ID_ANY;
            enableFlashlight = true;
            videoFormat = "mp4";
            sensitivity = 25;
            sizeThreshold = 0.1;
            serverPort = 5000;

            // Update view
            updateView();
        });

        // Connect Save button
        findViewById(R.id.settingsSaveBtn).setOnClickListener(view -> saveSettings());

        // Connect Back button
        findViewById(R.id.settingsBackBtn).setOnClickListener(view -> onPause());

        // Connect storage spinner
        spinnerStorages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView
                    , int position, long id) {
                externalFilesDir = externalFilesDirs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        // Connect camera ID spinner
        cameraIDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView
                    , int position, long id) {
                if (position == 1)
                    cameraID = CameraBridgeViewBase.CAMERA_ID_BACK;
                else if (position == 2)
                    cameraID = CameraBridgeViewBase.CAMERA_ID_FRONT;
                else
                    cameraID = CameraBridgeViewBase.CAMERA_ID_ANY;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }
        });

        // Connect enable flashlight switch
        switchFlashlight.setOnCheckedChangeListener((compoundButton, b) ->
                enableFlashlight = switchFlashlight.isChecked());

        // Connect video format spinner
        formatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView
                    , int position, long id) {
                if (position == 1)
                    videoFormat = "mkv";
                else
                    videoFormat = "mp4";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // Connect sensitivity slider
        sensitivitySlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                sensitivity = (int) slider.getValue();
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                sensitivity = (int) slider.getValue();
            }
        });

        // Connect size threshold slider
        sizeThresholdSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                sizeThreshold = slider.getValue();
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                sizeThreshold = slider.getValue();
            }
        });

        // Connect server port editTest
        serverPortText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    serverPort = Integer.parseInt(charSequence.toString());
                } catch (Exception ignored) { }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    serverPort = Integer.parseInt(editable.toString());
                } catch (Exception ignored) { }
            }
        });

        // Copy settings to local variables
        this.externalFilesDir = SettingsContainer.externalFilesDir;
        this.cameraID = SettingsContainer.cameraID;
        this.enableFlashlight = SettingsContainer.enableFlashlight;
        this.videoFormat = SettingsContainer.videoFormat;
        this.sensitivity = SettingsContainer.sensitivity;
        this.sizeThreshold = SettingsContainer.sizeThreshold;
        this.serverPort = SettingsContainer.serverPort;

        // Load view
        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Start main activity on pause
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        System.gc();
        finish();
    }

    /**
     * Updates activity elements with local settings variables
     */
    private void updateView() {
        // Storage
        spinnerStorages.setAdapter(new ArrayAdapter<>(this,
                R.layout.spinner_layout, R.id.textViewSpinner, externalFilesDirs));
        if (externalFilesDirs.contains(externalFilesDir))
            spinnerStorages.setSelection(externalFilesDirs.indexOf(externalFilesDir));

        // Camera index
        cameraIDSpinner.setAdapter(new ArrayAdapter<>(this,
                R.layout.spinner_layout, R.id.textViewSpinner, cameraOptions));
        if (cameraID == CameraBridgeViewBase.CAMERA_ID_BACK)
            cameraIDSpinner.setSelection(1);
        else if (cameraID == CameraBridgeViewBase.CAMERA_ID_FRONT)
            cameraIDSpinner.setSelection(2);
        else
            cameraIDSpinner.setSelection(0);

        // Enable flashlight
        switchFlashlight.setChecked(enableFlashlight);

        // Video format
        formatSpinner.setAdapter(new ArrayAdapter<>(this,
                R.layout.spinner_layout, R.id.textViewSpinner, videoFormats));
        if (videoFormats.contains(videoFormat))
            formatSpinner.setSelection(videoFormats.indexOf(videoFormat));

        // Sensitivity
        sensitivitySlider.setValue((float) sensitivity);

        // Size threshold
        sizeThresholdSlider.setValue((float) sizeThreshold);

        // Server port
        serverPortText.setText(String.valueOf(serverPort));
    }

    /**
     * Assigns local settings to a SettingsContainer class and calls SettingsHandler.saveSettings()
     * to save the settings to a JSON file
     */
    private void saveSettings() {
        try {
            // Copy settings from local variables
            SettingsContainer.externalFilesDir = this.externalFilesDir;
            SettingsContainer.cameraID = this.cameraID;
            SettingsContainer.enableFlashlight = this.enableFlashlight;
            SettingsContainer.videoFormat = this.videoFormat;
            SettingsContainer.sensitivity = this.sensitivity;
            SettingsContainer.sizeThreshold = this.sizeThreshold;
            SettingsContainer.serverPort = this.serverPort;

            // Save settings to file
            SettingsHandler.saveSettings(MainActivity.settingsFile, this);
            Toast.makeText(this, R.string.settings_saved,
                    Toast.LENGTH_SHORT).show();

            // Update web server if port changed
            if (WebServer.isServerListening()
                    && WebServer.serverPort != SettingsContainer.serverPort) {
                try {
                    WebServer.setServerPort(SettingsContainer.serverPort);
                    WebServer.stopServer();
                    WebServer.startServer(this);
                } catch (Exception ignored) { }
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_wrong_settings,
                    Toast.LENGTH_LONG).show();
            Log.e(TAG, "Wrong settings provided!", e);
        }
    }
}