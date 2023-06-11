package com.alibaba.datax.core.transport.transformer;

import com.alibaba.datax.common.element.Column;
import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.element.StringColumn;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.transformer.Transformer;

import java.util.Arrays;

public class ReplaceAllTransformer extends Transformer {

    public ReplaceAllTransformer() {
        setTransformerName("dx_replace_all");
    }
    @Override
    public Record evaluate(Record record, Object... paras) {
        int columnIndex;
        String before;
        String after;
        try {
            if (paras.length != 3) {
                throw new RuntimeException("dx_replace_all paras must be 3");
            }

            columnIndex = (Integer) paras[0];
            before = (String) paras[1];
            after = (String) paras[2];

            if (before.length() == 0) {
                throw new RuntimeException("dx_replace_all before:(%s) can not be empty");
            }
            if (before.length() >= 10 || after.length() >= 10) {
                throw new RuntimeException(String.format("dx_replace_all param out of range 10, before: %s, after: %s", before.length(), after.length()));
            }
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_ILLEGAL_PARAMETER, "paras:" + Arrays.asList(paras).toString() + " => " + e.getMessage());
        }

        try {
            if (columnIndex < 0) {
                for (int curIndex = 0; curIndex < record.getColumnNumber(); curIndex++) {
                    Column column = record.getColumn(curIndex);
                    String newValue = replaceValue(column, before, after);
                    record.setColumn(curIndex, new StringColumn(newValue));
                }
            } else if (columnIndex < record.getColumnNumber()) {
                Column column = record.getColumn(columnIndex);
                String newValue = replaceValue(column, before, after);
                record.setColumn(columnIndex, new StringColumn(newValue));
            }
        } catch (Exception e) {
            throw DataXException.asDataXException(TransformerErrorCode.TRANSFORMER_RUN_EXCEPTION, e.getMessage(),e);
        }
        return record;
    }

    private String replaceValue(Column column, String before, String after) {
        String oriValue = column.asString();
        //如果字段为空，跳过replace处理
        if (oriValue != null) {
            return oriValue.replaceAll(before, after);
        }
        return oriValue;
    }

    public static void main(String[] args) {
        Long aLong = 1L;
        String val = "a\tb\n".toString().replaceAll("\t","\\\\t");
        String val1 = "a\n\tb\n".toString().replaceAll("\n\t",",");
        String val2 = "a|b".toString().replaceAll("\\|",",");
        System.out.println(val);
    }

}
