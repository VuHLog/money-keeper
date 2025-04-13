export const formatCurrency = (amount, currency = '₫') => {
  if (amount === null || amount === undefined) return '0 ' + currency;
  
  // Chuyển đổi sang số và làm tròn
  const number = Number(amount);
  if (isNaN(number)) return '0 ' + currency;

  // Format số với dấu phân cách hàng nghìn
  const formattedNumber = number.toLocaleString('vi-VN');
  
  // Thêm ký hiệu tiền tệ
  return `${formattedNumber} ${currency}`;
};

export const formatCurrencyWithColor = (amount, currency = '₫', color = 'text-blue-accent-3') => {
  const formattedAmount = formatCurrency(amount, currency);
  return `<span class="${color} font-weight-bold">${formattedAmount}</span>`;
}; 

export const formatNumberToPercent = (numerator, denominator) => {
  return `(${(Math.round((numerator * 10000 / denominator))/100).toString().replace('.', ',')}%)`;
}; 