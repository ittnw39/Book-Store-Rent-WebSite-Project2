const usernameInput = document.querySelector("#usernameInput");
const emailInput = document.querySelector("#emailInput");
const passwordInput = document.querySelector("#passwordInput");
const submitButton = document.querySelector("#submitButton");

submitButton.addEventListener("click", handleRegister);

async function handleRegister(e) {
    e.preventDefault();

    const username = usernameInput.value;
    const email = emailInput.value;
    const password = passwordInput.value;

    try {
        const data = { username, email, password };
        const response = await fetch("/users/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            console.log(result); // Handle successful registration response
        } else {
            console.error("Registration failed");
        }
    } catch (error) {
        console.error(error);
    }
}