
/** Assets内のデータを読み込み、Jsonのデータとして保存。
 *  Jsonのデータを呼び出して表示させる
 *  AsyncTaskLoaderを使用
 *  表示データ【title】、【a_created_at】、【sha】、【全体】
 */

package com.example.assets_json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;


public class AssetsAsyncActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<JSONObject>, OnClickListener{
	/*Bundle保存（取出）用KEY*/
	private static final String KEY_URL_STR = "urlStr";
	/*取得用URL*/
	static private final String ASSET_PATH = "Untitled.txt";
	JSONObject jsonObject;			//JSONObject型の変数をここで先に宣言。

	@Override
	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		setContentView(R.layout.assets_async);
		findViewById(R.id.btn_referesh).setOnClickListener(this);
		Bundle args = new Bundle(1);
		args.putString(KEY_URL_STR,ASSET_PATH);
		getSupportLoaderManager().restartLoader(0, args, this);
	}
	/** Loaderが正しく生成されたときに呼び出される。
	 */
	@Override
	public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
		ProgressDialogFragment dialog = new ProgressDialogFragment();	/*プログレスダイアログ表示*/
		Bundle pa = new Bundle();
		pa.putString("message", "データを読み込んでいますよ。");
		dialog.setArguments(pa);
		dialog.show(getSupportFragmentManager(), "progress");
		String urlStr = args.getString(KEY_URL_STR);					/*KEY情報を基にBundle内のURLを取り出す。*/
		if (! TextUtils.isEmpty(urlStr)) {
			return new AsyncJSONLoader(getApplication(), urlStr);		/*メインアクティビティのContextを渡す。*/
		}
		return null;
	}

	/** loader内の処理が終了したときに呼び出される。
	 */
	@Override
	public void onLoadFinished(Loader<JSONObject> loader, JSONObject data) {
		/*プログレスダイアログを閉じる */
		ProgressDialogFragment dialog = (ProgressDialogFragment)getSupportFragmentManager().findFragmentByTag("progress");
		if (dialog != null) {/*ダイアログが存在している時のみ消す*/
			dialog.onDismiss(dialog.getDialog());
		}
		/*ここで取得したJSONObjectを変数に代入して保持。*/
		jsonObject = data;
	}

	/**　loaderがリセットされた時に呼び出される。
	 */
	@Override
	public void onLoaderReset(Loader<JSONObject> data) {
		/*特に何もしない*/
	}

	/**取得ボタンが押された場合の処理*/
	public void onClick(View v) {
		if (v.getId() == R.id.btn_referesh) {

			/** assetsの中のデータ取得
			 * 
			 */
			/*ここからassetsのデータを読み込む*/
			AssetManager as = getResources().getAssets(); 
			InputStream is = null;  
			BufferedReader br = null;  
			StringBuilder sb = new StringBuilder();   
			try{  
				try {            	
					is = as.open(ASSET_PATH);
					br = new BufferedReader(new InputStreamReader(is));   
					String str;     
					while((str = br.readLine()) != null){     
						sb.append(str +"\n");
					}      
				} finally {  
					if (br != null) br.close();  
				}  
			} catch (IOException e) {  /*assetsのデータ取得に失敗した時に表示*/
				e.printStackTrace();
				Toast.makeText(this, "読み込み失敗", Toast.LENGTH_SHORT).show();  
			} 
			String assetString =  new String(sb);
			
			/*assetsの中のデータをJSONObjectに入れて、getStringで取り出す*/
			try {
				JSONObject assetObject = new JSONObject(assetString);
				String a_title = assetObject.getString("title");
				String a_created_at = assetObject.getString("created_at");
				JSONObject descObj = assetObject.getJSONObject("head");
				String a_sha = descObj.getString("sha");
				
				TextView a_titleView = (TextView) findViewById(R.id.a_2);
				TextView a_created_atView = (TextView) findViewById(R.id.a_3);
				TextView a_shaView = (TextView) findViewById(R.id.a_4);
				a_titleView.setText("【title】\n"+a_title);
				a_created_atView.setText("【a_created_at】\n"+a_created_at);
				a_shaView.setText("【sha】\n"+a_sha);
				TextView label = (TextView)this.findViewById(R.id.label);  
				label.setText("【全体】\n"+sb.toString());
				
			} catch (JSONException e2) {
				e2.printStackTrace();
			}		
		}
	}
	
	public void onNothingSelected(AdapterView<?> parent) {
		/* NOP */
	}
}