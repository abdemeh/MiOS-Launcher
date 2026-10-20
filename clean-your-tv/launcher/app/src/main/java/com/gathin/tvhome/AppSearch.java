package com.gathin.tvhome;
import java.text.Normalizer;
import java.util.Locale;
/** Local app search tolerates accents, mixed case and reordered search terms. */
final class AppSearch {
    private static String normalize(String value){return Normalizer.normalize(value,Normalizer.Form.NFKD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);}
    static boolean matches(String label,String query){
        if(query==null || query.trim().isEmpty())return true;
        if(label==null)return false;
        String target=normalize(label);
        for(String token:normalize(query).trim().split("\\s+"))if(!target.contains(token))return false;
        return true;
    }
    private AppSearch(){}
}
