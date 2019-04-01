package com.kingavatar.menuapp;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MenuDatabase";
    private static final String TABLE_NAME = "Menu";

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Menu (_id INTEGER PRIMARY KEY AUTOINCREMENT, Type TEXT, Description TEXT , Monday TEXT, Tuesday TEXT, Wednesday TEXT, Thursday TEXT, Friday TEXT, Saturday TEXT, Sunday TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Menu");

        onCreate(sqLiteDatabase);
    }

    public void addItems(Context context, String type, int Day, String des, String item) {
        ContentValues values = new ContentValues(3);
        values.put("Type", type);
        values.put("Description", des);
        switch (Day) {
            case 1:
                values.put("Monday", item);
                break;
            case 2:
                values.put("Tuesday", item);
                break;
            case 3:
                values.put("Wednesday", item);
                break;
            case 4:
                values.put("Thursday", item);
                break;
            case 5:
                values.put("Friday", item);
                break;
            case 6:
                values.put("Saturday", item);
                break;
            case 7:
                values.put("Sunday", item);
                break;
            default: //Toast.makeText(context,"Wrong File format Upload different File",Toast.LENGTH_LONG).show();
                break;
        }
        getWritableDatabase().insert("Menu", "Type", values);
    }

    Cursor getitems(int Day, String Type) throws InvalidFormatException {
        String[] columns = new String[]{"Type", "Description", "Monday"};
        String whereClause = "Type = ?";
        switch (Day) {
            case 1:
                break;
            case 2:
                columns[2] = "Tuesday";
                break;
            case 3:
                columns[2] = "Wednesday";
                break;
            case 4:
                columns[2] = "Thursday";
                break;
            case 5:
                columns[2] = "Friday";
                break;
            case 6:
                columns[2] = "Saturday";
                break;
            case 0:
                columns[2] = "Sunday";
                break;
            default:
                throw new InvalidFormatException("Day was wrong index given " + Integer.toString(Day));
        }
        String[] whereArgs = new String[]{Type};
        return getReadableDatabase().query("Menu", columns, whereClause, whereArgs, null, null, null);
    }

    public void deleteAll() {
        getWritableDatabase().execSQL("DELETE from " + TABLE_NAME);
        getWritableDatabase().execSQL("VACUUM");
    }

    String excelDatabase(Sheet sheet) {
        int descol = 2, titleindex = 0;
        ContentValues contentValues = new ContentValues();
        int[] dayscol = new int[7];
        StringBuilder stringBuilder = new StringBuilder();
        onUpgrade(getWritableDatabase(), 0, 1);
        long rowId = 0;
        for (Iterator<Row> rit = sheet.rowIterator(); rit.hasNext(); ) {
            Row row = rit.next();
            if (row == null) continue;
            if (row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("Day") ||
                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("Description") ||
                    row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue().equalsIgnoreCase("Monday")) {
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    row.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
                    if (row.getCell(i, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim().equalsIgnoreCase("Description")) {
                        descol = i;
                        titleindex = row.getRowNum();
                    } else if (row.getCell(i, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim().equalsIgnoreCase("Monday"))
                        dayscol[0] = i;
                    else if (row.getCell(i, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim().equalsIgnoreCase("Tuesday"))
                        dayscol[1] = i;
                    else if (row.getCell(i, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim().equalsIgnoreCase("Wednesday"))
                        dayscol[2] = i;
                    else if (row.getCell(i, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim().equalsIgnoreCase("Thursday"))
                        dayscol[3] = i;
                    else if (row.getCell(i, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim().equalsIgnoreCase("Friday"))
                        dayscol[4] = i;
                    else if (row.getCell(i, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim().equalsIgnoreCase("Saturday"))
                        dayscol[5] = i;
                    else if (row.getCell(i, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim().equalsIgnoreCase("Sunday"))
                        dayscol[6] = i;
                }
                break;
            }

        }
        StringBuilder sbd = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            sbd.append(" ").append(Integer.toString(dayscol[i])).append(" ");
        }
        Log.d("Indexs", "Descol " + Integer.toString(descol) + " Days Col" + sbd.toString() + "Title Index " + Integer.toString(titleindex));
        String[] typ = {"Breakfast", "Lunch", "Dinner"};
        int temptyp = 0;
        for (int i = titleindex + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || temptyp >= 3) continue;
            String item;
            String sqlqury = "INSERT INTO Menu (Type, Description, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday) VALUES (";
            stringBuilder.setLength(0);
            stringBuilder.append(sqlqury);
            row.getCell(descol, Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
            item = row.getCell(descol, Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim();
            if (!item.equalsIgnoreCase("MILK") && !item.equalsIgnoreCase("SAUNF")) {
                stringBuilder.append("\"").append(typ[temptyp]).append("\"").append(",");
            } else stringBuilder.append("\"").append(typ[temptyp++]).append("\"").append(",");
            stringBuilder.append("\"").append(item).append("\"").append(",");
            row.getCell(dayscol[0], Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
            item = row.getCell(dayscol[0], Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim();
            stringBuilder.append("\"").append(item).append("\"").append(",");
            row.getCell(dayscol[1], Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
            item = row.getCell(dayscol[1], Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim();
            stringBuilder.append("\"").append(item).append("\"").append(",");
            row.getCell(dayscol[2], Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
            item = row.getCell(dayscol[2], Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim();
            stringBuilder.append("\"").append(item).append("\"").append(",");
            row.getCell(dayscol[3], Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
            item = row.getCell(dayscol[3], Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim();
            stringBuilder.append("\"").append(item).append("\"").append(",");
            row.getCell(dayscol[4], Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
            item = row.getCell(dayscol[4], Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim();
            stringBuilder.append("\"").append(item).append("\"").append(",");
            row.getCell(dayscol[5], Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
            item = row.getCell(dayscol[5], Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim();
            stringBuilder.append("\"").append(item).append("\"").append(",");
            row.getCell(dayscol[6], Row.CREATE_NULL_AS_BLANK).setCellType(Cell.CELL_TYPE_STRING);
            item = row.getCell(dayscol[6], Row.CREATE_NULL_AS_BLANK).getRichStringCellValue().getString().trim();
            stringBuilder.append("\"").append(item).append("\"");
            stringBuilder.append(")");
            sqlqury = stringBuilder.toString();
            //Log.d("iteming","String is "+stringBuilder.toString()+" ");
            //statement = db.compileStatement(sqlqury);
            getWritableDatabase().execSQL(sqlqury);
            //databseuploader.doProgress((i*100)/(sheet.getLastRowNum()+1));
            //statement.bindString(1,stringBuilder.toString());
            //rowId = statement.executeInsert();
        }
        Log.d("Indexs", "title index is " + Integer.toString(titleindex) + " " + Integer.toString(descol) + " " + Integer.toString(dayscol[0]));
        //getWritableDatabase().insert("Menu","Description",contentValues);
        return "Database Created ";//+"rowId is "+Float.toString(rowId);
    }
}