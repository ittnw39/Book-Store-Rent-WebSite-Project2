const emailInput = document.querySelector("#emailInput");
const passwordInput = document.querySelector("#passwordInput");
const submitButton = document.querySelector("#submitButton");

submitButton.addEventListener("click", handleLogin);

async function handleLogin(e) {
    e.preventDefault();

    const email = emailInput.value;
    const password = passwordInput.value;

    try {
        const data = { email, password };
        const response = await fetch("/users/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            console.log(result); // Handle successful login response
        } else {
            console.error("Login failed");
        }
    } catch (error) {
        console.error(error);
    }
}