package io.nghlong3004.penny.formatter;

import io.nghlong3004.penny.model.Transaction;
import io.nghlong3004.penny.model.TransactionSummary;
import io.nghlong3004.penny.model.type.CommandType;
import io.nghlong3004.penny.model.type.TransactionType;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ResponseMessageBuilder {
    private final NumberFormat currencyFormatter;

    private final DateTimeFormatter shortDateFormatter;

    public ResponseMessageBuilder() {
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.shortDateFormatter = DateTimeFormatter.ofPattern("dd/MM");
    }

    public String formatSummary(CommandType type, List<TransactionSummary> summaryList) {

        String title = switch (type) {
            case DAILY, WEEKLY, MONTHLY -> type.getDetail();
            default -> null;
        };

        if (summaryList == null || summaryList.isEmpty()) {
            return title + "\n\nBạn chưa có giao dịch nào trong thời gian này.";
        }

        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (TransactionSummary summary : summaryList) {
            if (summary.type() == TransactionType.INCOME) {
                totalIncome = summary.totalAmount();
            }
            else {
                totalExpense = summary.totalAmount();
            }
        }

        double netTotal = totalIncome - totalExpense;
        String netEmoji = netTotal >= 0 ? "✅" : "🔻";

        return "<b>" + title + "</b>\n\n" + "Tổng thu: \n" + "<code>  + " + currencyFormatter.format(
                totalIncome) + "</code>\n\n" + "Tổng chi: \n" + "<code>  - " + currencyFormatter.format(
                totalExpense) + "</code>\n\n" + "-------------------------------\n" + netEmoji + " Còn lại: \n" + "<code>  " + currencyFormatter.format(
                netTotal) + "</code>";
    }

    public String formatFoundTransactions(String searchTerm, List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return String.format("Không tìm thấy giao dịch nào khớp với '%s'.", searchTerm);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<b>Kết quả tìm kiếm cho '%s':</b>\n\n", searchTerm));

        for (Transaction tx : transactions) {
            String dateStr = shortDateFormatter.format(tx.created().toLocalDateTime());
            String typeStr = (tx.type() == TransactionType.INCOME) ? "Thu" : "Chi";
            String amountStr = currencyFormatter.format(tx.amount());

            sb.append(String.format("(%s) %s %s - %s\n", dateStr, typeStr, amountStr, tx.description()));
        }

        return sb.toString();
    }

    public String formatUndoMessage(Transaction undoneTransaction) {
        if (undoneTransaction == null) {
            return "Không có giao dịch nào gần đây để hoàn tác.";
        }

        String typeStr = (undoneTransaction.type() == TransactionType.INCOME) ? "Thu" : "Chi";
        String amountStr = currencyFormatter.format(undoneTransaction.amount());

        StringBuilder sb = new StringBuilder();
        sb.append("<b>Hoàn tác thành công!</b>\n\n");
        sb.append("Đã xóa giao dịch:\n");
        sb.append(String.format("<code>%s %s - %s</code>", typeStr, amountStr, undoneTransaction.description()));

        return sb.toString();
    }

    public String formatDeletePrompt(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "Bạn chưa có giao dịch nào gần đây để xóa.";
        }
        return "<b>Vui lòng chọn giao dịch bạn muốn XÓA:</b>\n(Chỉ hiển thị 5 giao dịch gần nhất)";
    }

}
