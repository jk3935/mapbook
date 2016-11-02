package sinny.proj.mapbook;

import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private AutoCompleteTextView searchField;
    private List<String> favorites;
    private File favoritesFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the map.
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);

        // Some instantiation or initialization will go here.
        searchField = (AutoCompleteTextView) findViewById(R.id.textfield_search);
        favoritesFile = new File("./favorites.txt");
        favorites = new ArrayList<String>();

        // Create new favorites file if it doesn't exist.
        if((!favoritesFile.exists()) && (!favoritesFile.isDirectory())){
            try {
                favoritesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Try to read the file and update the list.
        String line = null;
        try {
            FileReader fileReader = new FileReader("favorites.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                favorites.add(line);
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        // Apply changes to the auto complete text view.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,favorites);
        searchField.setAdapter(adapter);
        searchField.setThreshold(3);

        // Sync the map.
        mapFragment.getMapAsync(this);
    }

    public void onSearch(View view){
        EditText textfieldSearchAddress = (EditText)findViewById(R.id.textfield_search);
        String searchAddress = textfieldSearchAddress.getText().toString();
        List<Address> addressList = null;

        if((searchAddress != null)&&(!searchAddress.equals(""))){
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(searchAddress, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(searchAddress));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        }
    }

    public void onFavorites(View view){
        // Update favorites list.
        EditText textfieldSearchAddress = (EditText)findViewById(R.id.textfield_search);
        String searchAddress = textfieldSearchAddress.getText().toString();
        favorites.add(searchAddress);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,favorites);
        searchField.setAdapter(adapter);

        // Write to the favorites file.
        try {
            FileWriter fileWriter = new FileWriter("favorites.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(searchAddress);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
