export function formatTime(time) {
    // 统一转换为Date对象
    const date = new Date(time);
    
    // 处理无效日期
    if (isNaN(date.getTime())) return 'Invalid Date';
  
    // 补零函数
    const padZero = num => num.toString().padStart(2, '0');
  
    // 分解时间组件
    const year = date.getFullYear();
    const month = padZero(date.getMonth() + 1);
    const day = padZero(date.getDate());
    const hours = padZero(date.getHours());
    const minutes = padZero(date.getMinutes());
    const seconds = padZero(date.getSeconds());
  
    // 拼接目标格式
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }
  