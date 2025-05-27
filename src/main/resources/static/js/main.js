document.addEventListener('DOMContentLoaded', async () => {
  try {
    const response = await fetch('/api/user');
    if (!response.ok) {
      throw new Error('Ошибка при получении данных');
    }
    const user = await response.json();

    document.getElementById('user-id').textContent = user.id;
    document.getElementById('user-first-name').textContent = user.firstName;
    document.getElementById('user-last-name').textContent = user.lastName;
    document.getElementById('user-username').textContent = user.username;
  } catch (error) {
    console.error('Ошибка:', error);
    alert('Не удалось загрузить данные пользователя');
  }
});