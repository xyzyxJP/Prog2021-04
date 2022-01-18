import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RankData {
    private static final String rankDataFileName = "rankData.json";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static File file = new File(rankDataFileName);
    private static ArrayList<Rank> rankList = new ArrayList<Rank>();

    RankData() {
        try {
            JSONObject jsonObject = new JSONObject(Files.readAllLines(Paths.get(rankDataFileName)));
            JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("rank"));
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    rankList.add(new Rank(new JSONObject(jsonArray.getString(i))));
                } catch (JSONException exception) {
                    System.err.println(exception.getMessage());
                } catch (ParseException exception) {
                    System.err.println(exception.getMessage());
                }
            }
        } catch (IOException exception) {
        }
    }

    public Rank SubmitScore(String name, int score) {
        Rank rank = new Rank(name, score);
        rankList.add(rank);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()),
                    "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < rankList.size(); i++) {
                JSONObject childJsonObject = new JSONObject();
                childJsonObject.put("name", rankList.get(i).name);
                childJsonObject.put("score", rankList.get(i).score);
                childJsonObject.put("rank", rankList.get(i).rank);
                childJsonObject.put("date", DATE_FORMAT.format(rankList.get(i).date));
                jsonArray.put(childJsonObject);
            }
            jsonObject.put("rank", jsonArray);
            bufferedWriter.write(jsonObject.toString());
            bufferedWriter.close();
        } catch (IOException exception) {
        }
        return rank;
    }

    public ArrayList<Rank> GetRankList() {
        Collections.sort(rankList, new RankComparator());
        return rankList;
    }

    class RankComparator implements Comparator<Rank> {

        public int compare(Rank rank1, Rank rank2) {
            int score1 = rank1.score;
            int score2 = rank2.score;
            long date1 = rank1.date.getTime();
            long date2 = rank2.date.getTime();
            if (score1 == score2) {
                if (date1 == date2) {
                    return 0;
                } else if (date1 < date2) {
                    return 1;
                } else {
                    return -1;
                }
            } else if (score1 < score2) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}