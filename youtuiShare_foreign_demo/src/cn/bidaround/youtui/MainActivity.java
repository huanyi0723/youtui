package cn.bidaround.youtui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import cn.bidaround.point.YtLog;
import cn.bidaround.youtui_template.YouTuiViewType;
import cn.bidaround.youtui_template.YtTemplate;
import cn.bidaround.ytcore.ErrorInfo;
import cn.bidaround.ytcore.YtCore;
import cn.bidaround.ytcore.YtShareListener;
import cn.bidaround.ytcore.data.ShareData;
import cn.bidaround.ytcore.data.YtPlatform;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.login.AuthUserInfo;

/**
 * @author youtui
 * @since 14/6/19
 * 
 */
public class MainActivity extends Activity  {
	
	private ShareData shareData;
	private YtTemplate template;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_demo);
		
		// Initialize the youtui configuration
		YtTemplate.init(this);
		
		// Application integration testing is completed, please delete the code or the parameter set to false
		//YtTemplate.checkConfig(true);
		
		init();
	}
	
	private void init() {
		// the initial login third party controls
		ViewGroup thirdloginviews = (ViewGroup) findViewById(R.id.thirdloginviews);
		thirdloginviews.addView(createImageView(R.drawable.yt_facebook));
		thirdloginviews.addView(createImageView(R.drawable.yt_twitter));
//		thirdloginviews.addView(createImageView(R.drawable.yt_vkontakte));
//		thirdloginviews.addView(createImageView(R.drawable.yt_tumblr));
//		thirdloginviews.addView(createImageView(R.drawable.yt_flickr));
//		thirdloginviews.addView(createImageView(R.drawable.yt_instagram));
//		thirdloginviews.addView(createImageView(R.drawable.yt_linkedin));
		
		initSpinner();
		initShareData();
	}
	
	//1 初始化要分享的数据
	private void initShareData(){
		shareData = new ShareData();
		shareData.setIsAppShare(false);
		shareData.setShareType(ShareData.SHARETYPE_IMAGEANDTEXT);
		shareData.setTitle(getString(R.string.title)); 
		shareData.setDescription(getString(R.string.des));
		//分享的文字
		shareData.setText(getString(R.string.text));
		shareData.setTargetUrl("http://youtui.mobi/");
		//图片URL地址
		shareData.setImageUrl("http://imgs.ebrun.com/resources/2014_02/2014_02_12/201402120001392169642448.jpg");
	}
	
	/**
	 * If don't want to display screenshots button
	 * template.setScreencapVisible(false);
	 * 
	 * If don't want to show toast
	 * template.setToastVisible(false);
	 * 
	 * If want to set the content of the each platform independence
	 * template.addData(YtPlatform.PLATFORM_MORE, shareData);
	 * 
	 * If want to set the share lisntener of the each platform independence
	 * template.addListener(YtPlatform.PLATFORM_MESSAGE, defaultShareListener);
	 * 
	 * If don't want to the button for point
	 * template.setPointShow(false);
	 * 
	 * If want to change popupwindow height
	 * template.setPopwindowHeight(400);
	 */
	private void initTemplate(int templateType){
		if(template == null)
			template = new YtTemplate(this, templateType);
		else
			template.setTemplateType(templateType);
		
		template.setShareData(shareData);
		template.addListeners(defaultShareListener);
		template.show();
	}
	
	/**
	 * the white grid 、white list 、black grid view click
	 */
	public void click(View v) {
		
		switch (v.getId()) {
		case R.id.popup_bt:
			initTemplate(YouTuiViewType.BLACK_POPUP);
			break;

		case R.id.list_bt:
			initTemplate(YouTuiViewType.WHITE_LIST);
			break;
			
		case R.id.main_whiteviewpager_bt:
			initTemplate(YouTuiViewType.WHITE_GRID);
			break;
			
		case R.id.white_grid_center:
			initTemplate(YouTuiViewType.WHITE_GRID_CENTER);
			break;
		}
	}
	
	// Third-party login to monitor events
	class ImageViewClick implements OnClickListener{
		
		@Override
		public void onClick(View v) {
			YtPlatform platform = null;
			switch (v.getId()) {
			case R.drawable.yt_facebook:
				platform = YtPlatform.PLATFORM_FACEBOOK;
				break;
				
			case R.drawable.yt_linkedin:
				platform = YtPlatform.PLATFORM_LINKEDIN;
				break;
				
			case R.drawable.yt_twitter:
				platform = YtPlatform.PLATFORM_TWITTER;
				break;
				
			case R.drawable.yt_flickr:
				platform = YtPlatform.PLATFORM_FLICKR;
				break;
				
			case R.drawable.yt_tumblr:
				platform = YtPlatform.PLATFORM_TUMBLR;
				break;
				
			case R.drawable.yt_instagram:
				platform = YtPlatform.PLATFORM_INSTAGRAM;
				break;
				
			case R.drawable.yt_vkontakte:
				platform = YtPlatform.PLATFORM_VKONTAKTE;
				break;
			}
			if(platform != null)
				doAuthorize(platform);
		}
	}
	
	// Authorize login call method
	private void doAuthorize(YtPlatform platform){
		YtCore.getInstance().authorize(this, platform, defaultAuthListener);
	}
	
	/**
	 * init spinner to select share image type
	 */
	private void initSpinner(){
		Spinner shareTypeSpiner = (Spinner) findViewById(R.id.share_type);
		
		String[] value = new String[]{getString(R.string.sharetype_imageandtext), 
				getString(R.string.sharetype_text),getString(R.string.sharetype_image)};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, value);
		shareTypeSpiner.setAdapter(adapter);
		
		shareTypeSpiner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 1:
					shareData.setShareType(ShareData.SHARETYPE_TEXT);
					break;
				case 2:
					shareData.setShareType(ShareData.SHARETYPE_IMAGE);
					break;
				case 0:
					shareData.setShareType(ShareData.SHARETYPE_IMAGEANDTEXT);
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
	}
	
	private ImageView createImageView(int backgroundRes){
		ImageView iv = new ImageView(this);
		int dp = getDp(50.0f);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp, dp);
		params.leftMargin = getDp(5.0f);
		params.rightMargin = getDp(5.0f);
		iv.setLayoutParams(params);
		iv.setBackgroundResource(backgroundRes);
		iv.setId(backgroundRes);
		iv.setOnClickListener(new ImageViewClick());
		return iv;
	}
	
	private int getDp(float value) {
		return (int) (getResources().getDisplayMetrics().density * value);
	}
	
	
	YtShareListener defaultShareListener = new YtShareListener() {

		@Override
		public void onSuccess(ErrorInfo error) {
			YtLog.w("Success, " + error.getErrorMessage());
		}

		@Override
		public void onPreShare() {
			YtLog.w("PerSharing, start...");
		}

		@Override
		public void onError(ErrorInfo error) {
			YtLog.w("Fail, " + error.getErrorMessage());
		}

		@Override
		public void onCancel() {
			YtLog.w("Canceling, Done...");
		}
	};
	
	AuthListener defaultAuthListener = new AuthListener() {
		
		@Override
		public void onAuthSucess(Activity act, AuthUserInfo userInfo) {
			YtLog.w(userInfo.toString());
		}
		
		@Override
		public void onAuthFail(Activity act) {
			YtLog.w("Call the authorize fail method");
		}
		
		@Override
		public void onAuthCancel(Activity act) {
			YtLog.w("Call the authorize cancel method");
		}
	};
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Release the youtui memory resources
        YtTemplate.release(this);
    }
}
