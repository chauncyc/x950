package com.example.criminalintent;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class ActionFragment extends Fragment {
	private Action mAction;
	private ActionLab mActionLab;
	private EditText mTitleField;
	private EditText mMinutesField;
	private EditText mOutcomeField;
	private EditText mContextField;
	
	private Button mDateButton;
	private boolean mChangesMade;
	private CheckBox mSolvedCheckBox;
	public static final String EXTRA_Action_ID = "com.example.criminalintent.Action_id";
	
	private static final String DIALOG_DATE = "date";
	private static final String DIALOG_OR_DATE = "dialog_or_date";
	private static final String DIALOG_TIME = "time";
	private static final int REQUEST_DATE = 2;
	private static final int REQUEST_DATE_OR_TIME = 1;
	private static final int REQUEST_TIME = 3;
	private static final int COMPLETED = 1;
	private static final int NOT_COMPLETED = 0;
	
		
	/*	Custom constructor that bundles an argument to a fragment,
		will be called by the code that creates this bundle. */
	public static ActionFragment newInstance(UUID ActionId){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_Action_ID, ActionId);
		
		ActionFragment fragment = new ActionFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mActionLab = ActionLab.get(getActivity());
		
		UUID ActionId = (UUID)getArguments().getSerializable(EXTRA_Action_ID);
		
		mAction = mActionLab.findAction(ActionId);
		mChangesMade = false;
		
		//used for the up button
		setHasOptionsMenu(true);
		
		
	}
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_fragment, menu);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, 
		Bundle savedInstanceState) {
		View v = (inflater.inflate(R.layout.fragment_action, parent, false));
		
		if(NavUtils.getParentActivityIntent(getActivity()) != null){
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		enableTextFields(v);
		
		
		
		mDateButton = (Button)v.findViewById(R.id.action_date);
		
		updateDate(mAction.getCreatedDate(), mAction.getCreatedDate());
		
		
		
		mDateButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				
				TimeOrDateFragment timeOrDate = new TimeOrDateFragment();
				timeOrDate.setTargetFragment(ActionFragment.this, REQUEST_DATE_OR_TIME);
				timeOrDate.show(fm, DIALOG_OR_DATE);
				mChangesMade = true;
			}
		});
				
		
		mSolvedCheckBox = (CheckBox)v.findViewById(R.id.action_solved);
		mSolvedCheckBox.setChecked((mAction.getActionStatus() == 1));
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//set the Actions solved property
				mAction.setActionStatus((isChecked) ? COMPLETED : NOT_COMPLETED);
				ActionLab.get(getActivity()).resetAction(mAction);
				mAction.getParent().verifyActionIncomplete();
				mChangesMade = true;
			}
		});
		
		
				
		return v;
	}
	
	private String mOutcomeTempName;
	
	private void enableTextFields(View v){
		mTitleField = (EditText)v.findViewById(R.id.action_title);
		mTitleField.setText(mAction.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, 
					int count) {
				mActionLab.changeActionTitle(mAction, c.toString());
				mChangesMade = true;
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after){
				//This space intentionally left blank
			}
			
			public void afterTextChanged(Editable c) {
				
			}
		});
		
		mMinutesField = (EditText)v.findViewById(R.id.minutes_to_complete);
		if(mAction.getMinutesExpected() != 0){
			mMinutesField.setText(String.valueOf(mAction.getMinutesExpected()));
		}
		mMinutesField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, 
					int count) {
				try{
					mAction.setMinutesExpected(Integer.parseInt(c.toString()));
				}catch(Exception e){
					//do nothing;
				}
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after){
				//This space intentionally left blank
			}
			
			public void afterTextChanged(Editable c) {
				//This also left blank
			}
		});
	
		mOutcomeField = (EditText)v.findViewById(R.id.outcome_text_field);
		mOutcomeField.setText(mAction.getParent().getTitle());
		mOutcomeField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, 
					int count) {
				mOutcomeTempName = c.toString();
				mChangesMade = true;
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after){
				//This space intentionally left blank
			}
			
			public void afterTextChanged(Editable c) {
				//Store the outcome in a temp field, will be saved when fragment paused.
				
			}
		});
	
		mContextField = (EditText)v.findViewById(R.id.context_text_field);
		mContextField.setText(mAction.getContextName());
		mContextField.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, 
					int count) {
				try{
					mAction.setContextName(c.toString());
				}catch(Exception e){
					//do nothing;
				}
				mChangesMade = true;
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after){
				//This space intentionally left blank
			}
			
			public void afterTextChanged(Editable c) {
				//This also left blank
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mChangesMade){
			mActionLab.updateParentInfo(mAction, mOutcomeTempName);
			mActionLab.saveActions();
		}
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode != Activity.RESULT_OK) return;
		switch(requestCode){
			case REQUEST_DATE_OR_TIME:
				FragmentManager fm = getActivity().getSupportFragmentManager();
				if((Integer)data.getSerializableExtra(TimeOrDateFragment.EXTRA_DATE_OR_TIME) == 2){
					DatePickerFragment dateDialog = DatePickerFragment.newInstance(mAction.getCreatedDate());
					dateDialog.setTargetFragment(ActionFragment.this, REQUEST_DATE);
					dateDialog.show(fm, DIALOG_DATE);
				}
				else if((Integer)data.getSerializableExtra(TimeOrDateFragment.EXTRA_DATE_OR_TIME) == 1){
					TimePickerFragment timeDialog = TimePickerFragment.newInstance(mAction.getCreatedDate());
					timeDialog.setTargetFragment(ActionFragment.this, REQUEST_TIME);
					timeDialog.show(fm, DIALOG_TIME);	
				}
				
				break;
			case REQUEST_DATE: 
				Date newDate = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
				updateDate(mAction.getCreatedDate(), newDate);
				mChangesMade = true;
				break;
				
			case REQUEST_TIME:
				Date newTime = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
				updateDate(newTime, mAction.getCreatedDate());
				mChangesMade = true;
				break;
				
			default:
				break;
		}
	}
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			if(NavUtils.getParentActivityIntent(getActivity()) != null){
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		case R.id.menu_item_delete:
			
			mActionLab.deleteAction(mAction);
			
			if(NavUtils.getParentActivityIntent(getActivity()) != null){
				NavUtils.navigateUpFromSameTask(getActivity());
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	private void updateDate(Date time, Date date){
		
		Calendar calendarDate = Calendar.getInstance();
		Calendar calendarTime = Calendar.getInstance();
		calendarDate.setTime(date);
		calendarTime.setTime(time);
		
		int hour = calendarTime.get(Calendar.HOUR);
		int minute = calendarTime.get(Calendar.MINUTE);
		int year = calendarDate.get(Calendar.YEAR);
		int month = calendarDate.get(Calendar.MONTH);
		int day = calendarDate.get(Calendar.DAY_OF_MONTH);
		
		mAction.setCreatedDate(new GregorianCalendar(year,month,day,hour,minute).getTime());
		
		mDateButton.setText(android.text.format.
				DateFormat.format("yyyy.MM.dd.HHmm", mAction.getCreatedDate()).toString());
	}
	
	
}
