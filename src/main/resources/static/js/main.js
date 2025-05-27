document.addEventListener('DOMContentLoaded', async () => {
  try {
    const response = await fetch('https://telegramauthwebapp.onrender.com/api/user', {
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Ошибка HTTP: ${response.status}`);
    }

    const user = await response.json();
    document.getElementById('user-id').textContent = user.id;
    document.getElementById('user-first-name').textContent = user.firstName;
    document.getElementById('user-last-name').textContent = user.lastName;
    document.getElementById('user-username').textContent = user.username;
  } catch (error) {
    console.error('Ошибка:', error);
    document.getElementById('user-profile').innerHTML = `
      <div class="text-red-500">
        Ошибка загрузки данных: ${error.message}
      </div>
    `;
  }
});