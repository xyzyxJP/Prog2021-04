
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Rank {
    public String name;
    public int score;
    public String rank;
    public Date date;

    Rank(JSONObject jsonObject) throws JSONException, ParseException {
        name = jsonObject.getString("name");
        score = jsonObject.getInt("score");
        rank = jsonObject.getString("rank");
        date = RankData.DATE_FORMAT.parse(jsonObject.getString("date"));
    }

    Rank(String name, int score) {
        this.name = name;
        this.score = score;
        if (score >= 15000) {
            rank = "S";
        } else if (score >= 12000) {
            rank = "A";
        } else if (score >= 9000) {
            rank = "B";
        } else if (score >= 6000) {
            rank = "C";
        } else if (score >= 3000) {
            rank = "D";
        } else {
            rank = "E";
        }
        date = new Date();
    }
}