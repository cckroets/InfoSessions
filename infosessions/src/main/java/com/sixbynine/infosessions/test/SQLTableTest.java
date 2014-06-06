package com.sixbynine.infosessions.test;

import com.sixbynine.infosessions.database.SQLTable;
import com.sixbynine.infosessions.database.SQLType;
import com.sixbynine.infosessions.database.WebData;

import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.testng.Assert.*;


/**
 * @author curtiskroetsch
 */
public class SQLTableTest
{
  final String NAME = "NAME";

  final SQLTable TABLE = new SQLTable.Builder(NAME)
          .addPrimaryKey("id", SQLType.INTEGER)
          .addKey("foobar", SQLType.TEXT)
          .build();

  @Test
  public void testCreateTable()
  {
    String ct = TABLE.createTableCommand();
    assertEquals(ct, "CREATE TABLE NAME(id INTEGER, foobar TEXT, PRIMARY KEY (id))");
  }

  @Test public void testDropTable()
  {
    String dt = TABLE.dropTableCommand();
    assertEquals(dt, "DROP TABLE IF EXISTS NAME");
  }

  @Test
  public void testSQLName()
  {
    assertEquals(TABLE.getName(), NAME);
  }

  @Test
  public void testSQLCalendar() throws Exception
  {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Date d = sdf.parse("21/12/2012 13:30");
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(d);

    String result = WebData.calendarToSQL(calendar);
    assertEquals(result, "2012-12-21 13:30:00");
  }

}
