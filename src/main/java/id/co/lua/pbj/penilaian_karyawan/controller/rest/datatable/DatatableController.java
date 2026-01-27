package id.co.lua.pbj.penilaian_karyawan.controller.rest.datatable;

import id.co.lua.pbj.penilaian_karyawan.controller.apps.BasicController;
import id.co.lua.pbj.penilaian_karyawan.utils.MyDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Controller
public class DatatableController extends BasicController {
    private static final Logger LOG = LogManager.getLogger(DatatableController.class);

    @Value("${spring.datasource.url}")
    String dbName;

    @Value("${spring.datasource.username}")
    String dbUser;

    @Value("${spring.datasource.password}")
    String dbPassword;

    public Map<String, Object> getJSONDataTableWithoutDistinct(
            Map<String, String> queryMap,
            String table,
            String innerJoinStatement,
            String additionalWhereClause,
            String[] aColumns,
            String groupClause,
            int[] searchAble,
            String withClause,
            String orderColumnDefault,
            int jenisDb //1 postgresql, 2 mysql
    ){
        StopWatch sw = new StopWatch();
        sw.start();

        if (innerJoinStatement == null) {
            innerJoinStatement = " ";
        }
        if (groupClause == null) {
            groupClause = " ";
        }

        // PAGING
        String sLimit = "";
        if (queryMap.get("iDisplayStart") != null && !queryMap.get("iDisplayLength").equalsIgnoreCase("-1")) {
            if(jenisDb == 1){
                sLimit = "OFFSET " + queryMap.get("iDisplayStart") + " LIMIT " + queryMap.get("iDisplayLength");
            }else if(jenisDb == 2){
                sLimit = "LIMIT " + queryMap.get("iDisplayStart") + "," + queryMap.get("iDisplayLength");
            }
        }

        // ORDERING
        String sOrder = "";
        if(orderColumnDefault!=null){
            sOrder = " order by "+orderColumnDefault;
        }
        if (queryMap.get("iSortCol_0") != null && !queryMap.get("iSortCol_0").equalsIgnoreCase("0") ) {
            sOrder = "ORDER BY  ";
            for (int i = 0; i < Integer.parseInt(queryMap.get("iSortingCols")); i++) {
                int iSortCol_ = Integer.parseInt(queryMap.get("iSortCol_" + i));
                String sSortDir_ = queryMap.get("sSortDir_" + i);
                if (queryMap.get("bSortable_" + iSortCol_).equalsIgnoreCase("true")) {
                    String column;
                    if(aColumns[i].contains(" as ")){
                        column=aColumns[iSortCol_].split(" as ")[1];
                    }else{
                        column=aColumns[iSortCol_];
                    }
                    sOrder += "" + column + " " + sSortDir_ + ", ";
                }else if(queryMap.get("iSortCol_0").equalsIgnoreCase("0")){
                    if (orderColumnDefault != null){
                        sOrder += "" + orderColumnDefault+" ";
                    }
                }
            }
            sOrder = sOrder.substring(0, sOrder.length() - 2);
            if (sOrder.equalsIgnoreCase("ORDER BY")) {
                sOrder = "";
            }
        }
        if(sOrder.contains(" as ")){
            sOrder=sOrder.split(" as ")[0];
        }

        // FILTERING
        String sWhere = "";
        if(searchAble == null){//Cari semua. Default
            if (queryMap.get("sSearch") != null && !queryMap.get("sSearch").isEmpty()) {
                sWhere = "WHERE (";
                for (int i = 0; i < aColumns.length; i++) {
                    String column;

                    if(aColumns[i].contains(" as ")){
                        column=aColumns[i].split(" as ")[0];
                    }else{
                        column=aColumns[i];
                    }
                    if(jenisDb == 1){
                        sWhere += "LOWER(" + column + "::varchar) LIKE LOWER('%" + queryMap.get("sSearch") + "%') OR ";
                    }else if(jenisDb ==2){
                        sWhere += "LOWER(CAST (" + column + " AS CHAR)) LIKE LOWER('%" + queryMap.get("sSearch") + "%') OR ";
                    }
                }
                sWhere = sWhere.substring(0, sWhere.length() - 3);
                sWhere += ")";
            }
        }else{
            if (queryMap.get("sSearch") != null && !queryMap.get("sSearch").isEmpty()) {
                sWhere = "WHERE (";
                for (int i = 0; i < aColumns.length; i++) {
                    for(int j =0; j < searchAble.length; j++){
                        if(i==searchAble[j]){
                            String column;

                            if(aColumns[i].contains(" as ")){
                                column=aColumns[i].split(" as ")[0];
                            }else{
                                column=aColumns[i];
                            }
                            if(jenisDb == 1){
                                sWhere += "LOWER(" + column + "::varchar) LIKE LOWER('%" + queryMap.get("sSearch") + "%') OR ";
                            }else if(jenisDb == 2){
                                sWhere += "LOWER(cast(" + column + " as char)) LIKE LOWER('%" + queryMap.get("sSearch") + "%') OR ";
                            }
                        }
                    }
                }
                sWhere = sWhere.substring(0, sWhere.length() - 3);
                sWhere += ")";
            }
        }

        // INDIVIDUAL COLUMN FILTERING
        for (int i = 0; i < aColumns.length; i++) {

            String bSearchable_ = queryMap.get("bSearchable_" + i);
            String sSearch_ = queryMap.get("sSearch_" + i);

            if (bSearchable_ != null && bSearchable_.equalsIgnoreCase("true") && !sSearch_.isEmpty()) {
                if (sWhere.isEmpty()) {
                    sWhere = "WHERE ";
                } else {
                    sWhere += " AND ";
                }
                String column;

                if(aColumns[i].contains(" as ")){
                    column=aColumns[i].split(" as ")[0];
                }else{
                    column=aColumns[i];
                }
                if(jenisDb == 1){
                    if(sSearch_.contains("#"))
                        sWhere += "LOWER(" + column + "::varchar) LIKE LOWER('" + sSearch_.substring(0,sSearch_.length()-1) + "') ";
                    else
                        sWhere += "LOWER(" + column + "::varchar) LIKE LOWER('%" + sSearch_ + "%') ";
                }else if(jenisDb == 2){
                    if(sSearch_.contains("#"))
                        sWhere += "LOWER(cast(" + column + " as char)) LIKE LOWER('" + sSearch_.substring(0,sSearch_.length()-1) + "') ";
                    else
                        sWhere += "LOWER(cast(" + column + " as char)) LIKE LOWER('%" + sSearch_ + "%') ";
                }
            }
        }

        String _implode = StringUtils.join(aColumns, ", ").replace(" , ", " ");
        String awal = aColumns[0];

        if(aColumns[0].contains(" as ")){
            awal=aColumns[0].split(" as ")[0];
        }

        // COUNT ALL RESULT WITHOUT LIMITATION
        // From MySQL Query Function : SQL_CALC_FOUND_ROWS
        String sQuery;
        if (!sWhere.isEmpty()) {
            sWhere = sWhere + (additionalWhereClause != null ? " AND " + additionalWhereClause : "");
        } else {
            sWhere = (additionalWhereClause != null ? " WHERE " + additionalWhereClause : "");
        }

        sQuery = "SELECT COUNT("+awal+") as count "
                + "FROM " + table+""+
                " "+innerJoinStatement+" "
                + sWhere+" ";

        if(withClause!=null) sQuery=withClause+"\n"+sQuery;

        LOG.info("rResultFilterTotal: "+sQuery);
        Map<String, Object> output = new HashMap<String, Object>();
        try (Connection conn = DriverManager.getConnection(
                dbName.split("\\?")[0] + "?serverTimezone=" + TimeZone.getDefault().getID(), dbUser, dbPassword);
             Statement stmt = conn.createStatement()
        ) {
            // Count result set
            int iFilteredTotal = 0;
            try (ResultSet rResultFilterTotal = stmt.executeQuery(sQuery)) {
                while (rResultFilterTotal.next()) {
                    iFilteredTotal = rResultFilterTotal.getInt("count");
                }
            }

            // FIND ALL RESULT WITH LIMITATION
            // From MySQL Query Function : SQL_FOUND_ROWS
            sQuery = "SELECT  " + _implode + " "
                    + "FROM " + table + " "
                    + innerJoinStatement + " " // PLUS INNER JOIN
                    + sWhere + " "
                    + groupClause + " "
                    + sOrder + " "
                    + sLimit;
            if (withClause != null) sQuery = withClause + "\n" + sQuery;
            //        Logger.info(sQuery);
            LOG.info(sQuery);

            int sEcho = 0;
            try (ResultSet rResult = stmt.executeQuery(sQuery)) {
                //sEcho = Integer.parseInt(queryMap.get("sEcho"));

                output.put("sEcho", sEcho);
                output.put("iTotalDisplayRecords", iFilteredTotal);

                ArrayList<String[]> rData = new ArrayList<String[]>();
                String[] tmp;

                while (rResult.next()) {
                    tmp = new String[aColumns.length];
                    for (int i = 0; i < aColumns.length; i++) {
                        Object obj;
                        // Get column name displayed by PostgreSQL Query Result
                        String column;
                        //                    if(aColumns[i].contains(" as ") && aColumns[i].contains(")")){
                        //                        column=aColumns[i].split("\\)")[1].split("as ")[1];
                        //                    }else
                        if (aColumns[i].contains(" as ") && aColumns[i].contains("+")) {
                            column = aColumns[i].split("\\+")[1].split("as ")[1];
                        } else if (aColumns[i].contains(" as ")) {
                            if (aColumns[i].contains("\\.")) {
                                column = aColumns[i].split("\\.")[1].split("as ")[1];
                            } else {
                                column = aColumns[i].split("as ")[1];
                            }
                        } else {
                            column = aColumns[i].split("\\.")[1];
                        }
                        obj = rResult.getObject(column);
                        String aRow;
                        if (obj instanceof Date) {
                            //                        aRow = FormatUtils.formatDateTimeInd((Date) obj);
                            aRow = String.valueOf(obj);
                        } else if (obj == null) {
                            aRow = "N/A";
                        } else {
                            aRow = String.valueOf(obj);
                        }

                        if (!aColumns[i].isEmpty() || aColumns[i].equalsIgnoreCase(" ")) {
                            tmp[i] = aRow;
                        } else {
                            tmp[i] = "N/A";
                        }
                    }
                    rData.add(tmp);
                }

                sw.stop();
                if(sw.getTotalTimeSeconds()>1) {
                    LOG.warn("Duration SQL: %s, SQL: %s", sw.getTotalTimeMillis(), sQuery);
                }
                output.put("aaData", rData);
            }
        } catch (SQLException e) {
            LOG.error("@DataTable.getJSONDataTable -> Tidak dapat eksekusi Result Set rResult! "+e);
            e.printStackTrace();
        }
        return output;
    }

    private Map<String, Object> getJSONDataTableWithoutDistinctOld(
            Map<String, String> queryMap,
            String table,
            String innerJoinStatement,
            String additionalWhereClause,
            String[] aColumns,
            String groupClause,
            int[] searchAble,
            String withClause,
            String orderColumnDefault,
            int jenisDb //1 postgresql, 2 mysql
    ) throws SQLException {

        if (innerJoinStatement == null) {
            innerJoinStatement = " ";
        }
        if (groupClause == null) {
            groupClause = " ";
        }

        // PAGING
        String sLimit = "";
        if (queryMap.get("iDisplayStart") != null && !queryMap.get("iDisplayLength").equalsIgnoreCase("-1")) {
            if(jenisDb == 1){
                sLimit = "OFFSET " + queryMap.get("iDisplayStart") + " LIMIT " + queryMap.get("iDisplayLength");
            }else if(jenisDb == 2){
                sLimit = "LIMIT " + queryMap.get("iDisplayStart") + "," + queryMap.get("iDisplayLength");
            }
        }

        // ORDERING
        String sOrder = "";
        if(orderColumnDefault!=null){
            sOrder = " order by "+orderColumnDefault;
        }
        if (queryMap.get("iSortCol_0") != null && !queryMap.get("iSortCol_0").equalsIgnoreCase("0") ) {
            sOrder = "ORDER BY  ";
            for (int i = 0; i < Integer.parseInt(queryMap.get("iSortingCols")); i++) {
                int iSortCol_ = Integer.parseInt(queryMap.get("iSortCol_" + i));
                String sSortDir_ = queryMap.get("sSortDir_" + i);
                if (queryMap.get("bSortable_" + iSortCol_).equalsIgnoreCase("true")) {
                    String column="";
                    if(aColumns[i].contains(" as ")){
                        column=aColumns[iSortCol_].split(" as ")[1];
                    }else{
                        column=aColumns[iSortCol_];
                    }
                    sOrder += "" + column + " " + sSortDir_ + ", ";
                }else if(queryMap.get("iSortCol_0").equalsIgnoreCase("0")){
                    if (orderColumnDefault != null){
                        sOrder += "" + orderColumnDefault+" ";
                    }
                }
            }
            sOrder = sOrder.substring(0, sOrder.length() - 2);
            if (sOrder.equalsIgnoreCase("ORDER BY")) {
                sOrder = "";
            }
        }
        if(sOrder.contains(" as ")){
            sOrder=sOrder.split(" as ")[0];
        }

        // FILTERING
        String sWhere = "";
        if(searchAble == null){//Cari semua. Default
            if (queryMap.get("sSearch") != null && !queryMap.get("sSearch").isEmpty()) {
                sWhere = "WHERE (";
                for (int i = 0; i < aColumns.length; i++) {
                    String column;

                    if(aColumns[i].contains(" as ")){
                        column=aColumns[i].split(" as ")[0];
                    }else{
                        column=aColumns[i];
                    }
                    if(jenisDb == 1){
                        sWhere += "LOWER(" + column + "::varchar) LIKE LOWER('%" + queryMap.get("sSearch") + "%') OR ";
                    }else if(jenisDb ==2){
                        sWhere += "LOWER(CAST (" + column + " AS CHAR)) LIKE LOWER('%" + queryMap.get("sSearch") + "%') OR ";
                    }
                }
                sWhere = sWhere.substring(0, sWhere.length() - 3);
                sWhere += ")";
            }
        }else{
            if (queryMap.get("sSearch") != null && !queryMap.get("sSearch").isEmpty()) {
                sWhere = "WHERE (";
                for (int i = 0; i < aColumns.length; i++) {
                    for(int j =0; j < searchAble.length; j++){
                        if(i==searchAble[j]){
                            String column;

                            if(aColumns[i].contains(" as ")){
                                column=aColumns[i].split(" as ")[0];
                            }else{
                                column=aColumns[i];
                            }
                            if(jenisDb == 1){
                                sWhere += "LOWER(" + column + "::varchar) LIKE LOWER('%" + queryMap.get("sSearch") + "%') OR ";
                            }else if(jenisDb == 2){
                                sWhere += "LOWER(cast(" + column + " as char)) LIKE LOWER('%" + queryMap.get("sSearch") + "%') OR ";
                            }
                        }
                    }
                }
                sWhere = sWhere.substring(0, sWhere.length() - 3);
                sWhere += ")";
            }
        }

        // INDIVIDUAL COLUMN FILTERING
        for (int i = 0; i < aColumns.length; i++) {

            String bSearchable_ = queryMap.get("bSearchable_" + i);
            String sSearch_ = queryMap.get("sSearch_" + i);

            if (bSearchable_ != null && bSearchable_.equalsIgnoreCase("true") && !sSearch_.isEmpty()) {
                if (sWhere.isEmpty()) {
                    sWhere = "WHERE ";
                } else {
                    sWhere += " AND ";
                }
                String column;

                if(aColumns[i].contains(" as ")){
                    column=aColumns[i].split(" as ")[0];
                }else{
                    column=aColumns[i];
                }
                if(jenisDb == 1){
                    if(sSearch_.contains("#"))
                        sWhere += "LOWER(" + column + "::varchar) LIKE LOWER('" + sSearch_.substring(0,sSearch_.length()-1) + "') ";
                    else
                        sWhere += "LOWER(" + column + "::varchar) LIKE LOWER('%" + sSearch_ + "%') ";
                }else if(jenisDb == 2){
                    if(sSearch_.contains("#"))
                        sWhere += "LOWER(cast(" + column + " as char)) LIKE LOWER('" + sSearch_.substring(0,sSearch_.length()-1) + "') ";
                    else
                        sWhere += "LOWER(cast(" + column + " as char)) LIKE LOWER('%" + sSearch_ + "%') ";
                }
            }
        }

        String _implode = StringUtils.join(aColumns, ", ").replace(" , ", " ");
        String awal = aColumns[0];

        if(aColumns[0].contains(" as ")){
            awal=aColumns[0].split(" as ")[0];
        }

        // COUNT ALL RESULT WITHOUT LIMITATION
        // From MySQL Query Function : SQL_CALC_FOUND_ROWS
        String sQuery;
        if (!sWhere.isEmpty()) {
            sWhere = sWhere + (additionalWhereClause != null ? " AND " + additionalWhereClause : "");
        } else {
            sWhere = (additionalWhereClause != null ? " WHERE " + additionalWhereClause : "");
        }

        sQuery = "SELECT COUNT("+awal+") as count "
                + "FROM " + table+""+
                " "+innerJoinStatement+" "
                + sWhere+" ";

        if(withClause!=null) sQuery=withClause+"\n"+sQuery;

        //Logger.info("rResultFilterTotal: "+sQuery);
//        Connection conn = DriverManager.getConnection(
//                dbName, dbUser, dbPassword);
        String dbName1 =dbName.split("\\?")[0];
        Connection conn = DriverManager.getConnection(
                dbName.split("\\?")[0]+"?serverTimezone=" + TimeZone.getDefault().getID(),dbUser,dbPassword);

        Statement stmt=conn.createStatement();
        ResultSet rResultFilterTotal = stmt.executeQuery(sQuery);

        // Count result set
        Integer iFilteredTotal = 0;
        try {
            while (rResultFilterTotal.next()) {
                iFilteredTotal = rResultFilterTotal.getInt("count");
            }
            rResultFilterTotal.close();
        } catch (SQLException e) {
//            Logger.info("@DataTable.getJSONDataTable -> Tidak dapat eksekusi Result Set rResultFilterTotal: ", e);
//            Logger.error("@DataTable.getJSONDataTable -> Tidak dapat eksekusi Result Set rResultFilterTotal!");
        }


        // FIND ALL RESULT WITH LIMITATION
        // From MySQL Query Function : SQL_FOUND_ROWS
        sQuery = "SELECT  " + _implode + " "
                + "FROM " + table + " "
                + innerJoinStatement + " " // PLUS INNER JOIN
                + sWhere + " "
                + groupClause+ " "
                + sOrder + " "
                + sLimit;
        if(withClause!=null) sQuery=withClause+"\n"+sQuery;
//        Logger.info(sQuery);
//        System.out.println(sQuery);
        ResultSet rResult = stmt.executeQuery(sQuery);

        Map<String, Object> output = new HashMap<String, Object>();
        Integer sEcho = 0;
        try {
            sEcho = Integer.parseInt(queryMap.get("sEcho"));
        } catch (NumberFormatException e) {
//            Logger.info("@DataTable.getJSONDataTable -> sEcho is NULL!: ", e);
//            Logger.error("@DataTable.getJSONDataTable -> sEcho is NULL!");
        }
        output.put("sEcho", sEcho);
        output.put("iTotalDisplayRecords", iFilteredTotal);

        ArrayList<String[]> rData = new ArrayList<String[]>();
        String[] tmp;
        try {
            while (rResult.next()) {
                tmp = new String[aColumns.length];
                for (int i = 0; i < aColumns.length; i++) {
                    Object obj;
                    // Get column name displayed by PostgreSQL Query Result
                    String column;
                    if(aColumns[i].contains(" as ") && aColumns[i].contains("+")){
                        column=aColumns[i].split("\\+")[1].split("as ")[1];
                    }else if(aColumns[i].contains(" as ")){
                        if(aColumns[i].contains("\\.")){
                            column=aColumns[i].split("\\.")[1].split("as ")[1];
                        }else{
                            column=aColumns[i].split("as ")[1];
                        }
                    }else{
                        column=aColumns[i].split("\\.")[1];
                    }
                    if (!innerJoinStatement.isEmpty()) {
                        obj = rResult.getObject(column);
                    } else {
                        obj = rResult.getObject(column);
                    }
                    String aRow;
                    if (obj instanceof Date) {
                        aRow = MyDateFormat.formatDateTimeInd((Date) obj);
                    } else if (obj == null) {
                        aRow = "N/A";
                    } else {
                        aRow = String.valueOf(obj);
                    }

                    if (!aColumns[i].isEmpty() || aColumns[i].equalsIgnoreCase(" ")) {
                        tmp[i] = aRow;
                    } else {
                        tmp[i] = "N/A";
                    }
                }
                rData.add(tmp);
            }
            rResult.close();
        } catch (SQLException e) {
//            Logger.error("@DataTable.getJSONDataTable -> Tidak dapat eksekusi Result Set rResult! "+e);
        }
//        sw.stop();
//        if(sw.getTime()>1000) {
//            Logger.warn("Duration SQL: %s, SQL: %s", sw, sQuery);
//        }
        output.put("aaData", rData);
        return output;
    }
}