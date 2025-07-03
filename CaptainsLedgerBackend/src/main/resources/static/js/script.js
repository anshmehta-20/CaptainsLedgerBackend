
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const loginMessage = document.getElementById('loginMessage');
    const registerMessage = document.getElementById('registerMessage');

	// Switch Logic:
	const toggleSwitch = document.getElementById('checkbox'); // Make sure this ID exists in your HTML
	    const currentTheme = localStorage.getItem('theme');

	    if (currentTheme) {
	        document.body.classList.add(currentTheme);
	        if (currentTheme === 'dark-mode') {
	            if (toggleSwitch) {
	                toggleSwitch.checked = true;
	            }
	        }
	    }

	    if (toggleSwitch) { // Ensure the toggle switch element exists
	        toggleSwitch.addEventListener('change', switchTheme, false);
	    }

	    function switchTheme(e) {
	        if (e.target.checked) {
	            document.body.classList.remove('light-mode');
	            document.body.classList.add('dark-mode');
	            localStorage.setItem('theme', 'dark-mode');
	        } else {
	            document.body.classList.remove('dark-mode');
	            document.body.classList.add('light-mode');
	            localStorage.setItem('theme', 'light-mode');
	        }
	    }

	
    // --- Login Form Logic ---
    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault(); // Prevent default form submission

            const username = document.getElementById('loginUsername').value;
            const password = document.getElementById('loginPassword').value;

            try {
                const response = await fetch('/api/auth/login', { // Assuming login endpoint is /api/auth/login
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ username, password })
                });

                if (response.ok) {
                    const data = await response.json(); // Assuming a successful login returns some data (e.g., token, user info)
                    // Store token or user info in localStorage/sessionStorage if needed
                    localStorage.setItem('authToken', data.token); // Example for token
                    localStorage.setItem('userRole', data.role); // Example for role
                    localStorage.setItem('username', username);

                    loginMessage.textContent = 'Login successful! Redirecting...';
                    loginMessage.style.color = 'green';
                    setTimeout(() => {
                        window.location.href = 'dashboard.html'; // Redirect to dashboard or appropriate page
                    }, 1000);
                } else {
                    const errorData = await response.json(); // Assuming backend sends JSON error
                    loginMessage.textContent = errorData.message || 'Login failed: Invalid credentials.';
                    loginMessage.style.color = 'red';
                }
            } catch (error) {
                console.error('Error during login:', error);
                loginMessage.textContent = 'An error occurred during login. Please try again.';
                loginMessage.style.color = 'red';
            }
        });
    }

    // --- Registration Form Logic ---
    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault(); // Prevent default form submission

            const username = document.getElementById('regUsername').value;
            const password = document.getElementById('regPassword').value;
            const confirmPassword = document.getElementById('regConfirmPassword').value;
            const email = document.getElementById('regEmail').value; // Get Email
            const role = document.getElementById('regRole').value; // Get Role

            if (password !== confirmPassword) {
                registerMessage.textContent = 'Passwords do not match!';
                registerMessage.style.color = 'red';
                return;
            }

            try {
                // Ensure the endpoint is correct for registration, based on your Postman test: /api/auth/register
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    // Send all required fields
                    body: JSON.stringify({ username, password, email, role })
                });

                if (response.ok) {
                    registerMessage.textContent = 'Registration successful! Redirecting to login...';
                    registerMessage.style.color = 'green';
                    setTimeout(() => {
                        window.location.href = 'index.html'; // Redirect to login page
                    }, 2000);
                } else {
                    const errorData = await response.json();
                    registerMessage.textContent = errorData.message || 'Registration failed.';
                    registerMessage.style.color = 'red';
                }
            } catch (error) {
                console.error('Error during registration:', error);
                registerMessage.textContent = 'An error occurred during registration. Please try again.';
                registerMessage.style.color = 'red';
            }
        });
    }
	
	// --- Dashboard Logic ---
	    if (window.location.pathname.endsWith('/dashboard.html')) {
	        const welcomeMessage = document.getElementById('welcomeMessage');
	        const avengerContent = document.getElementById('avengerContent');
	        const adminContent = document.getElementById('adminContent');
	        const logoutButton = document.getElementById('logoutButton');

	        const authToken = localStorage.getItem('authToken');
	        const userRole = localStorage.getItem('userRole');
	        const username = localStorage.getItem('username');

	        // Redirect if not authenticated
	        if (!authToken) {
	            window.location.href = 'index.html';
	            return; // Stop execution
	        }

	        // Display welcome message
	        if (welcomeMessage && username) {
	            welcomeMessage.textContent = `Welcome, ${username}!`;
	        }

	        // Show/hide content based on role
	        if (avengerContent && adminContent) {
	            if (userRole === 'ADMIN') {
	                avengerContent.style.display = 'none';
	                adminContent.style.display = 'block'; // Make sure it's block or flex, not just 'initial'
	            } else if (userRole === 'AVENGER') {
	                avengerContent.style.display = 'block'; // Make sure it's block or flex
	                adminContent.style.display = 'none';
	            } else {
	                // Handle unknown roles or default to Avenger view for safety
	                avengerContent.style.display = 'block';
	                adminContent.style.display = 'none';
	            }
	        }

	        // Logout functionality
	        if (logoutButton) {
	            logoutButton.addEventListener('click', () => {
	                localStorage.removeItem('authToken');
	                localStorage.removeItem('userRole');
	                localStorage.removeItem('username');
	                window.location.href = 'index.html';
	            });
	        }
	    }
});