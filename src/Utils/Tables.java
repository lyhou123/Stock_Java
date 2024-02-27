package Utils;

import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.List;


public class Tables{
    public static void renderTable(List<String> menu) {
        Table table = new Table(1, BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.SURROUND_HEADER_FOOTER_AND_COLUMNS);
        CellStyle centerAlign = new CellStyle(CellStyle.HorizontalAlign.center);
        table.setColumnWidth(0,173,173);
        table.addCell(menu.toString(),centerAlign);
        System.out.println(table.render());

    }
}
