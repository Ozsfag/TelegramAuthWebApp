document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/user', {
            credentials: 'include',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (response.status === 401) {
            window.location.href = '/';
            return;
        }

        if (!response.ok) {
            throw new Error(`Ошибка HTTP: ${response.status}`);
        }

        const user = await response.json();

        const setText = (id, value) => {
            const element = document.getElementById(id);
            if (element) element.textContent = value || 'N/A';
        };

        setText('user-id', user.uuid);  // Используем uuid вместо id
        setText('user-first-name', user.firstName);
        setText('user-last-name', user.lastName);
        setText('user-username', user.username);

    } catch (error) {
        console.error('Ошибка:', error);
        const profileDiv = document.getElementById('user-profile');
        if (profileDiv) {
            profileDiv.innerHTML = `
                <div class="text-red-500 p-4">
                    ${error.message}<br>
                    <a href="/" class="text-blue-500">Попробовать снова</a>
                </div>
            `;
        }
    }
});