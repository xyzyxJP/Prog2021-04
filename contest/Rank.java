
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Rank {
    public String name;
    public int score;
    public String rank;
    public Date date;

    /**
     * ランクを生成する (JSONObject)
     * 
     * @param jsonObject JSONObject
     * @throws JSONException
     * @throws ParseException
     */
    Rank(JSONObject jsonObject) throws JSONException, ParseException {
        // JSONオブジェクトからランクデータを読み込む
        name = jsonObject.getString("name");
        score = jsonObject.getInt("score");
        rank = jsonObject.getString("rank");
        date = RankData.DATE_FORMAT.parse(jsonObject.getString("date"));
    }

    /**
     * ランクを生成する (int)
     * 
     * @param score スコア
     */
    Rank(int score) {
        this.name = System.getenv("USERNAME");
        this.score = score;
        // スコアによってランク分けをする
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

    /**
     * 名前 + スコア + (ランク) + 日付を返す
     */
    @Override
    public String toString() {
        return name + "   " + score + " (" + rank + ") " + RankData.DATE_FORMAT.format(date);
    }
}