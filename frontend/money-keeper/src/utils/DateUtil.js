export const getWeekday = (dateString) => {
  const date = new Date(dateString);
  const today = new Date();

  // Tạo ngày hôm qua
  const yesterday = new Date();
  yesterday.setDate(today.getDate() - 1);

  // Chuyển đổi về dạng YYYY-MM-DD để so sánh
  const formatDate = (d) => d.toISOString().split("T")[0];

  if (formatDate(date) === formatDate(today)) {
    return "Hôm nay";
  }

  if (formatDate(date) === formatDate(yesterday)) {
    return "Hôm qua";
  }

  const weekdays = [
    "Chủ Nhật",
    "Thứ Hai",
    "Thứ Ba",
    "Thứ Tư",
    "Thứ Năm",
    "Thứ Sáu",
    "Thứ Bảy",
  ];
  return weekdays[date.getDay()];
};

export const formatDate = (date) => {
  return date instanceof Date
    ? new Date(date.getTime() - (date.getTimezoneOffset() * 60000))
      .toISOString()
      .slice(0, 19)
      .replace('T', ' ')
    : date;
};

export const parseDateString = (dateStr) => {
  const [datePart] = dateStr.split(' ');
  const [yyyy, mm, dd] = datePart.split('-');
  return new Date(parseInt(yyyy), parseInt(mm) - 1, parseInt(dd)); //yyyy-mm-dd
}