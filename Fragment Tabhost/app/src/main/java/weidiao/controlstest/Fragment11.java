package weidiao.controlstest;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment11 extends BaseFragment {

	public static BaseFragment newInstance(int index) { 
		BaseFragment fragment = new Fragment11();
		Bundle args = new Bundle();
		args.putInt("index", index);
		fragment.setArguments(args);
		fragment.setIndex(index);
		return fragment;
	}

	private View layoutView;
	private FragmentTabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		layoutView = inflater.inflate(R.layout.fragment1, null);
		
		TextView tv = (TextView)layoutView.findViewById(R.id.textView111);
		tv.setText(getIndex()+"");
		return layoutView;
	}
}
