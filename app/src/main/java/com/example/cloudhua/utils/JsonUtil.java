package com.example.cloudhua.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cloudhua on 16-7-30.
 */
public class JsonUtil {
    public enum ParseType {
        CREATEUSER , GETPRODUCTS , CREATEORDER , JOINTEAM , CREATETEAM , LISTTEAMS ,GETMYTEAMS
        ,GETALLTEAMMEMBERS , DELETETEAM , GETUSERORDERS ,GETUSERORDERSBYTEAMID ,CANCELORDER
        ,QUITFROMTEAM , CLEARALLMYHISTORYORDERS , FINISHTEAMORDER , NOTIFYALLTEAMMEMBERS;
    }

    public static String[][] parseFor(String json , ParseType type) {
        if (json != null && !"".equals(json)) {
            try {
                JSONObject object = new JSONObject(json);
                switch (type) {
                    case CREATEUSER:
                        String info1[][] = new String[1][];
                        info1[0] = new String[]{object.getString("stat"), object.getString("id")};
                        return info1;
                    case GETPRODUCTS:
                        JSONArray array = object.getJSONArray("products");
                        String info2[][] = new String[array.length()][];
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object2 = array.getJSONObject(i);
                            info2[i] = new String[]{object2.getString("id"), object2.getString("price"), object2.getString("name"),};
                        }
                        return info2;
                    case CREATEORDER:
                    case CREATETEAM:
                    case DELETETEAM:
                    case JOINTEAM:
                    case CANCELORDER:
                    case QUITFROMTEAM:
                    case CLEARALLMYHISTORYORDERS:
                    case FINISHTEAMORDER:
                    case NOTIFYALLTEAMMEMBERS:
                        String info3[][] = new String[1][];
                        info3[0] = new String[]{object.getString("stat")};
                        return info3;

                    case LISTTEAMS:
                        JSONArray array5 = object.getJSONArray("teams");
                        String info5[][] = new String[array5.length()][];
                        for (int i = 0; i < array5.length(); i++) {
                            JSONObject object5 = array5.getJSONObject(i);
                            info5[i] = new String[]{object5.getString("teamName"), object5.getString("creatorName"),object5.getString("creatorTEL")
                                    , object5.getString("creatorID"), object5.getString("teamID")};
                        }
                        return info5;
                    case GETMYTEAMS:
                        JSONArray array6 = object.getJSONArray("teams");
                        String info6[][] = new String[array6.length()][];
                        for (int i = 0; i < array6.length(); i++) {
                            JSONObject object6 = array6.getJSONObject(i);
                            info6[i] = new String[]{object6.getString("teamName"), object6.getString("creatorName"),object6.getString("creatorTEL")
                                    , object6.getString("creatorID"), object6.getString("tid")};
                        }
                        return info6;
                    case GETALLTEAMMEMBERS:
                        JSONArray array7 = object.getJSONArray("usersInformationBelongsToThisTeam");
                        String info7[][] = new String[array7.length()][];
                        for (int i = 0; i < array7.length(); i++) {
                            JSONObject object7 = array7.getJSONObject(i);
                            info7[i] = new String[]{object7.getString("userName"),object7.getString("userTEL"),object7.getString("userID")};
                        }
                        return info7;
                    case GETUSERORDERS:
                        JSONArray array8 = object.getJSONArray("orders");
                        String info8[][] = new String[array8.length()][];
                        for (int i = 0; i < array8.length(); i++) {
                            JSONObject object8 = array8.getJSONObject(i);
                            info8[i] = new String[]{object8.getString("orderID"),object8.getString("productNum"),
                                    object8.getString("belongsToTeam"),object8.getString("orderTime"),
                                    object8.getString("orderStatus"),object8.getString("productPrice"),
                                    object8.getString("productName"),object8.getString("productID")};
                        }
                        return info8;
                    case GETUSERORDERSBYTEAMID:
                        JSONArray array9 = object.getJSONArray("teams");
                        String info9[][] = new String[array9.length()][];
                        for (int i = 0; i < array9.length(); i++) {
                            JSONObject object9 = array9.getJSONObject(i);
                            info9[i] = new String[]{object9.getString("userID"),object9.getString("orderID"),
                                    object9.getString("priductPrice"),object9.getString("productNum"),
                                    object9.getString("userName"),object9.getString("orderTime"),
                                    object9.getString("productName"),object9.getString("productID"),
                                    object9.getString("userTEL")};
                        }
                        return info9;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }
}
