package com.abdalkarimalbiekdev.herafi.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.abdalkarimalbiekdev.herafi.Security.AES;
import com.abdalkarimalbiekdev.herafi.model.CraftSelectedIModel;
import com.abdalkarimalbiekdev.herafi.Remote.HerafiAPI;
import com.abdalkarimalbiekdev.herafi.Remote.RetrofitClient;
import com.abdalkarimalbiekdev.herafi.networkModel.CraftmanData;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;


public class Common {

    public static final String SECRET_KEY_WORD = ".......................";
    public static final String HCAPTCHA_SITE_KEY = "...........................";
    public static final String AGORA_APP_ID = "...........................";
    public static final String AGORA_TEMPORARY_TOKEN = "....................................................................";
    public static final String AGORA_CHANNEL_NAME = ".................";
    public static final String CLIEN_ID_GOOGLE_SIGNIN = "................................................................";

    public static String BASE_URL = "...................................................";
    public static String BASE_PHOTO = "..................................................";

    public static String TOKEN_PUBLIC_KEY = "...............................................................................................................................................................................................";

//    public static String token;
    public static String FINGERPRINT_KEY = "...............................................";
    public static String MAPBOX_ACCESS_TOKEN = "...................................................................";

    public static CraftmanData craftmanData;

    //    public static MutableLiveData<List<CraftSelectedIModel>> selectedCrafts = new MutableLiveData<>();
    public static List<CraftSelectedIModel> selectedCraftsData = new ArrayList<>();
    public static RecyclerView recyclerExp;
    public static int lastNewBooking;


    public static HerafiAPI getAPI(){
        return RetrofitClient.getInstance(BASE_URL).create(HerafiAPI.class);
    }

    public static boolean isConnectionToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){

                for (int i = 0; i < info.length; i++) {

                    Log.d("check_internet" , "OK");

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String rsaPublicKey = TOKEN_PUBLIC_KEY;
        rsaPublicKey = rsaPublicKey.replace(".............................", "");
        rsaPublicKey = rsaPublicKey.replace("..............................", "");

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(rsaPublicKey , Base64.DEFAULT));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(keySpec);

        return publicKey;
    }

    public static boolean checkSecureToken(String newToken){

        Claims payload = null;
        boolean status = false;

        try {
            payload = Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    .parseClaimsJws(newToken)
                    .getBody();

            if (AES.decrypt(SECRET_KEY_WORD).equals(AES.decrypt(payload.get("................." , String.class)))){

                Log.d("verify_token" , "Token is right");
                status =  true;
            }
            else{
                Log.d("verify_not_token" , "Token is hacker");
                status = false;
            }

        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.d("AlgorithmException" , e.getMessage());
        }
        catch (InvalidKeySpecException e) {
            e.printStackTrace();
            Log.d("InvalidKeySpecException" , e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("token_error" , e.getMessage());
        }

        return status;

    }

}
