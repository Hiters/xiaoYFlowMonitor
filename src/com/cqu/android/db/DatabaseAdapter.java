package com.cqu.android.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	
	// ���ݿ�������
	private final static String DATABASE_NAME = "Flow.db";
	// ����
	private final static String TABLE_NAME = "table1"; 
	//������ID
	private final static String TABLE_ID = "FlowID";
	//������������λbyte
	private final static String TABLE_UPF = "UpFlow";
	//������������λbyte
	private final static String TABLE_DPF = "DownFlow";
	//��������
	//��ʽ��YYYY-MM-DD HH:MM:SS
	private final static String TABLE_TIME = "Time";
	//�������ͣ���WIFI��GPRS
	private final static String TABLE_WEB = "WebType";
	//���ݿ�汾��
	private final static int DB_VERSION = 1;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM");
	
	private Context mContext = null;
	
	//���������䣬���ڵ�һ�δ������ݿ�ʱ��������     1 ����id integer����      2������������long  3��������integer 4ʱ��datetime����
	private final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ " (" + TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ TABLE_UPF + " Long," + TABLE_DPF
			+ " Long," + TABLE_WEB + " INTEGER," + TABLE_TIME + " DATETIME)";
	// ���ݿ����
		private SQLiteDatabase mSQLiteDatabase = null;

	//���ݿ�adapter�����ڴ������ݿ�
	private DatabaseHelper mDatabaseHelper = null;
    //�Զ������ݿ�Helper   �̳��˳�����helper  ʵ���˷���
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DB_VERSION);
		}
		
		/*
		 * �������ݿ�
		 * ������
		 * */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(CREATE_TABLE);
//			String insertData = " INSERT INTO " + TABLE_NAME + " (" + TABLE_ID
//					+  ", " + TABLE_UPF + ", " + TABLE_DPF
//					+ "," + TABLE_WEB + "," + TABLE_TIME + " ) values(" + 1
//					+ ",' '," + 0 + ", " + 0 + "," + 3 + ","
//					+ new java.sql.Date(1) + ");";
//			db.execSQL(insertData);
		}

		/*
		 * ���ݿ����
		 *ɾ�������´����±� 
		 * */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	
	//�ⲿ����
	/*�ⲿ�� ���캯�� ��ȡcontext */
	public DatabaseAdapter(Context context) {
		mContext = context;
	}

	// �����ݿ⣬�������ݿ����
	public void open() throws SQLException {
		mDatabaseHelper = new DatabaseHelper(mContext);
		 /**
	     * Create and/or open a database that will be used for reading and writing.
	     * The first time this is called, the database will be opened and
	     * {@link #onCreate}, {@link #onUpgrade} and/or {@link #onOpen} will be
	     * called.     public SQLiteDatabase getWritableDatabase() 
	     */
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	// �ر����ݿ�
	public void close() {
		mDatabaseHelper.close();
	}

	/* ����һ������ ��������Ϣ����Ϊ��λ*/
	public void insertData(long UpFlow, long DownFlow,
			int WebType, Date date) {
		String dataString = sdf.format(date);
		String insertData = " INSERT INTO " + TABLE_NAME + " ("
		+ TABLE_UPF + ", " + TABLE_DPF + "," + TABLE_WEB + ","
				+ TABLE_TIME + " ) values(" + UpFlow + ", "
				+ DownFlow + "," + WebType + "," + "datetime('" + dataString
				+ "'));";
		mSQLiteDatabase.execSQL(insertData);
		
	}
	
	//////////////��������
	
	 public void updateData(long upFlow,long downFlow, int webType, Date date){
			String dataString = sdf.format(date);
			String updateData = " UPDATE " + TABLE_NAME + " SET "+ TABLE_UPF+"=" +upFlow+" , " +TABLE_DPF+"="+downFlow+
	        " WHERE " + TABLE_WEB+"=" + webType+" and "+ TABLE_TIME +" like '"+dataString+"%'"; 
			mSQLiteDatabase.execSQL(updateData);
	 }
			
			
	/////////////////
   
	 /*����Ƿ���ڸ�������*/
		public Cursor check( int netType, Date date) {
			String dataString = sdf.format(date);
			//Query the given table, returning a {@link Cursor} over the result set.
			/* public Cursor query(String table, String[] columns, String selection,
            String[] selectionArgs, String groupBy, String having,
            String orderBy, String limit) */

			Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
					TABLE_UPF+" AS upPro",TABLE_DPF+" AS dwPro"},  TABLE_WEB + "=" + netType+" and "+ TABLE_TIME +" like '"+dataString+"%'", null, null,
					null, null, null);// dateת��
			return mCursor;
		}
///////////////////////////////	

		/* ��ѯ������������ */
		public Cursor fetchDayFlow(int year, int month, int day, int netType) {
			StringBuffer date = new StringBuffer();
			date.append(String.valueOf(year) + "-");
			if (month < 10) {
				date.append("0" + String.valueOf(month) + "-");
			} else {
				date.append(String.valueOf(month) + "-");
			}
			if (day < 10) {
				date.append("0" + String.valueOf(day));
			} else {
				date.append(String.valueOf(day));
			}
			Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
					"sum(" + TABLE_UPF + ") AS sumUp",
					"sum(" + TABLE_DPF + ") as sumDw" }, TABLE_WEB + "=" + netType
					+ " and " + TABLE_TIME + " LIKE '" + date.toString() + "%'",
					null, null, null, null, null);
			return mCursor;
		}

	/* ��ѯÿ������ �������±����������ͳ�� */
	public Cursor fetchMonthFlow(int year, int Month, int netType) {
		StringBuffer date = new StringBuffer();
		date.append(String.valueOf(year) + "-");
		if (Month < 10) {
			date.append("0" + String.valueOf(Month) + "-");
		} else {
			date.append(String.valueOf(Month) + "-");
		}
		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
				"sum(" + TABLE_UPF + ") AS monthUp",
				"sum(" + TABLE_DPF + ") as monthDw" }, TABLE_WEB + "="
				+ netType + " and " + TABLE_TIME + " LIKE '" + date.toString()
				+ "%'", null, null, null, null, null);
//		mCursor.close();
		return mCursor;
	}
   //����ĳ����ϴ�����
	public long getProFlowUp(int netType, Date date){
		//���ڲ�����������Ϊ��λ����ÿһ�е����ݶ���һ���������Ϣ
		Cursor cur = check( netType, date);
		long UP=0 ;
     /**
	   * This interface provides random read-write access to the result set returned
	   * by a database query.
	   *
	   * Cursor implementations are not required to be synchronized so code using a Cursor from multiple
	   * threads should perform its own synchronization when using the Cursor.
	   */
		  /**
	     * Returns the current position of the cursor in the row set.
	     * The value is zero-based. When the row set is first returned the cursor
	     * will be at positon -1, which is before the first row. After the
	     * last row is returned another call to next() will leave the cursor past
	     * the last entry, at a position of count().
	     *
	     * @return the current cursor position.      int getPosition();
	     */
	

		if(cur.moveToNext()){
			
			/**
		     * Returns the zero-based index for the given column name, or -1 if the column doesn't exist.
		     * If you expect the column to exist use {@link #getColumnIndexOrThrow(String)} instead, which
		     * will make the error more clear.
		     *
		     * @param columnName the name of the target column.
		     * @return the zero-based column index for the given column name, or -1 if
		     * the column name does not exist.
		     * @see #getColumnIndexOrThrow(String)
		     */
			int up = cur.getColumnIndex("upPro");
			
			 /**
		     * Returns the value of the requested column as a long.
		     *
		     * <p>The result and whether this method throws an exception when the
		     * column value is null, the column type is not an integral type, or the
		     * integer value is outside the range [<code>Long.MIN_VALUE</code>,
		     * <code>Long.MAX_VALUE</code>] is implementation-defined.
		     *
		     * @param columnIndex the zero-based index of the target column.
		     * @return the value of that column as a long.   long getLong(int columnIndex);
		     */
		    
			UP = cur.getLong(up);
		}	
		cur.close();
		return UP ;
		
	}
	//����ĳ�����������
	public long getProFlowDw(int netType, Date date){
		Cursor cur = check( netType, date);
		long UP=0 ;
		if(cur.moveToNext()){
			int up = cur.getColumnIndex("dwPro");
			UP = cur.getLong(up);
		}
		cur.close();
			return UP ;
	}
	/* ����ÿ�յ����� */
	public long calculate(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int upColumn = calCurso.getColumnIndex("sumUp");
					int dwColumn = calCurso.getColumnIndex("sumDw");
					sum = calCurso.getLong(upColumn)
							+ calCurso.getLong(dwColumn);
				} while (calCurso.moveToNext());
			}
		}
//		calCurso.close();
		return sum;
	}
//////////���㱾���ϴ�����/////////////////////////////////
	public long weekUpFloew(int netType){
		/**
	     * Constructs a new {@code GregorianCalendar} initialized to the current date and
	     * time with the default {@code Locale} and {@code TimeZone}.
	     *  public static synchronized Calendar getInstance() {
                   return new GregorianCalendar();
             }
              public GregorianCalendar() {
                 this(TimeZone.getDefault(), Locale.getDefault());
            }

	     */
		//��ȡϵͳ����
		Calendar date1 = Calendar.getInstance();
		/*set(f, value) �������ֶ� f ����Ϊ value�����⣬��������һ���ڲ���Ա������
		��ָʾ�����ֶ� f �Ѿ������ġ����������ֶ� f ���������ĵģ�
		����ֱ���´ε��� get()��getTime()��getTimeInMillis()��add()
		�� roll() ʱ�Ż����¼���������ʱ��ֵ���Ժ���Ϊ��λ����
		��ˣ���ε��� set() ���ᴥ����β���Ҫ�ļ��㡣
		
		*add(f, delta) �� delta ��ӵ� f �ֶ��С�
		*���ͬ�ڵ��� set(f, get(f) + delta)����Ҫ����������������

         Add ���� 1�����ú� f �ֶε�ֵ��ȥ����ǰ f �ֶε�ֵ���� delta��
                          ���ֶ� f �з������κ����Ϊģ������������ֶ�ֵ�����䷶Χʱ��
                          �������һ��������ֶλ������ݼ��������ֶ�ֵ�������䷶Χ�ڡ�

         Add ���� 2��
                        �������ĳһ����С���ֶ��ǲ���ģ�������������ǰ��ֵ�ǲ����ܵģ�
                        ��Ϊ���ֶ� f ��������֮�󣬻����ڳ�������Լ��֮�󣬱���ʱ��ƫ�����������ģ�
                        �������ֵ����СֵҲ�ڷ������ģ�Ȼ������ֵ������Ϊ�����ӽ�����������ֵ��
                        ��С���ֶα�ʾһ����С��ʱ�䵥Ԫ��HOUR ��һ���� DAY_OF_MONTH С���ֶΡ�
                        ���ڲ������ǲ����ֶεĸ�С�ֶΣ���������κε���������ϵͳ��ȷ�������������Щ�ֶΡ�

                           ���⣬�� set() ��ͬ��add() ǿ������ϵͳ�������¼��������ĺ������������ֶΡ�

                        ʾ�����ٶ� GregorianCalendar ���������Ϊ 1999 �� 8 �� 31 �ա�
                        ���� add(Calendar.MONTH, 13) ����������Ϊ 2000 �� 9 �� 30 �ա�
        Add ���� 1 �� MONTH �ֶ�����Ϊ September��
                       ��Ϊ�� August ��� 13 ���µó��ľ�����һ��� September��
                       ��Ϊ�� GregorianCalendar �У�DAY_OF_MONTH �������� 9 �� 31 �գ�
                       ���� add ���� 2 �� DAY_OF_MONTH ����Ϊ 30��������ܵ�ֵ��
                       ��������һ����С���ֶΣ������ܸ��ݹ��� 2 ���� DAY_OF_WEEK��
                       ��Ϊ�� GregorianCalendar �е��·ݷ����仯ʱ����ֵҲ��Ҫ�����仯��
		*/
		//��������Ϊ���ܵĵ�һ��
		date1.set(Calendar.DAY_OF_WEEK, date1.getFirstDayOfWeek());
		long flowUp = 0 ;
		for (int i=0 ; i<7 ; i++){
			
			int y = date1.get(Calendar.YEAR);
			int m = date1.get(Calendar.MONTH)+1;
			int d = date1.get(Calendar.DAY_OF_MONTH); 
			flowUp +=calculateUp(y, m, d,  netType);
			date1.add(Calendar.DAY_OF_WEEK, 1);     
			
		}
		return flowUp ;
	}
	
	//���㱾����������
	
	public long weekDownFloew(int netType){
		Calendar date1 = Calendar.getInstance();//�õ����ڵ�����
		date1.set(Calendar.DAY_OF_WEEK, date1.getFirstDayOfWeek());//�����ڸ�Ϊ���������ܵĵ�һ��
		long flowDw = 0 ;
		for (int i=0 ; i<7 ; i++){
			
			int y = date1.get(Calendar.YEAR);
			int m = date1.get(Calendar.MONTH)+1;
			int d = date1.get(Calendar.DAY_OF_MONTH);
			flowDw +=calculateDw(y, m, d,  netType);
			date1.add(Calendar.DAY_OF_WEEK, 1);   //date1��һ��  	
		}
		
		return flowDw ;
	}

	/////////////////////////////////////////////
	//����ÿ���ϴ�����
	public long calculateUpForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum = 0;
		
			if (lCursor != null) {
				if (lCursor.moveToFirst()) {
					do {
						int upColumn = lCursor.getColumnIndex("monthUp");
						sum += lCursor.getLong(upColumn);
					} while (lCursor.moveToNext());
				}
				lCursor.close();
		}
		return sum;
	}
	//����ÿ����������
	public long calculateDnForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum =0;
		
			if (lCursor != null) {
				if (lCursor.moveToFirst()) {
					do {
						int dwColumn = lCursor.getColumnIndex("monthDw");
						sum += lCursor.getLong(dwColumn);
					} while (lCursor.moveToNext());
				}
				lCursor.close();
		}
		return sum;
	}
	/* ����ĳ�µ����� */
	public long calculateForMonth(int year, int Month, int netType) {
		Cursor lCursor = fetchMonthFlow(year, Month, netType);
		long sum;
		long monthSum = 0;
		
			if (lCursor != null) {
				if (lCursor.moveToFirst()) {
					do {
						int upColumn = lCursor.getColumnIndex("monthUp");
						int dwColumn = lCursor.getColumnIndex("monthDw");
						sum = lCursor.getLong(upColumn) + lCursor.getLong(dwColumn);
						monthSum += sum;
					} while (lCursor.moveToNext());
				}
				lCursor.close();
		}
		return monthSum;
	}



/* ����ÿ�յ��ϴ����� */
	public long calculateUp(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int upColumn = calCurso.getColumnIndex("sumUp");
					sum = calCurso.getLong(upColumn);
				} while (calCurso.moveToNext());
			}
		}
//		calCurso.close();
		return sum;
	}
	/* ����ÿ�յ�xiazai���� */
	public long calculateDw(int year, int month, int day, int netType) {
		Cursor calCurso = fetchDayFlow(year, month, day, netType);
		long sum = 0;
		if (calCurso != null) {
			if (calCurso.moveToFirst()) {
				do {
					int dwColumn = calCurso.getColumnIndex("sumDw");
					sum = calCurso.getLong(dwColumn);
				} while (calCurso.moveToNext());
			}
		}
//		calCurso.close();
		return sum;
	}

	
	/* ����ÿ����������� */
//	public Cursor programmeCur(String proName, int netType) {
//		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
//				TABLE_PRO, "sum(" + TABLE_UPF + ") AS upPro",
//				"sum(" + TABLE_DPF + ") AS dwPro" }, TABLE_PRO + "= '"
//				+ proName + "' and " + TABLE_WEB + "=" + netType, null, null,
//				null, null, null);// dateת��
//		return mCursor;
//	}

	/* ����ÿ����������� */
//	public long calPro(String proName, int netType) {
//		Cursor cursor = programmeCur(proName, netType);
//		long upFlow;
//		long downFlow;
//		long flow = 0;
//		long countFlow = 0;
//		if (cursor.moveToFirst()) {
//			do {
//				int upCol = cursor.getColumnIndex("upPro");
//				int downCol = cursor.getColumnIndex("dwPro");
//				upFlow = cursor.getLong(upCol);
//				downFlow = cursor.getLong(downCol);
//				flow = upFlow + downFlow;
//				countFlow += flow;
//			} while (cursor.moveToNext());
//		}
//		cursor.close();
//		return countFlow;
//	}

	/* �������г�������� */
//	public Cursor allProgrammeCur(int netType) {
//		Cursor mCursor = mSQLiteDatabase.query(TABLE_NAME, new String[] {
//				TABLE_PRO, "sum(" + TABLE_UPF + ") AS upPro",
//				"sum(" + TABLE_DPF + ") AS dwPro" }, TABLE_WEB + "=" + netType,
//				null, null, TABLE_PRO, null, null);// dateת��
//		return mCursor;
//	}
//
//	public ProgrammeHolder[] calAllPro(int netType) {
//		Cursor cursor = allProgrammeCur(netType);
//		int count = cursor.getCount();
//		ProgrammeHolder[] programmeHolder = new ProgrammeHolder[count];
//		long upFlow;
//		long downFlow;
//		long flow;
//		String proName;
//		ProgrammeHolder ph ;
//		int  i =0;
//		if (cursor.moveToFirst()) {
//			do {
//				flow =0;
//				
//				int proNameId = cursor.getColumnIndex(TABLE_PRO);
//				int upCol = cursor.getColumnIndex("upPro");
//				int downCol = cursor.getColumnIndex("dwPro");
//				
//				proName  = cursor.getString(proNameId);
//				upFlow = cursor.getLong(upCol);
//				downFlow = cursor.getLong(downCol);
//				flow = upFlow + downFlow;
//				
//				ph = new ProgrammeHolder();
//				ph.setTraffic(flow);
//				ph.setName(proName);
//				
//				programmeHolder[i] = ph;
//				i++;
//			} while (cursor.moveToNext());
//		}
//		cursor.close();
//		return programmeHolder;
//	}

	/* ����һ������ �Ժ���չ�� */

	/* ������� */
	public void deleteAll() {
		mSQLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
	}
	public void clear(){
		mSQLiteDatabase.delete(TABLE_NAME, null, null);
	}
}
