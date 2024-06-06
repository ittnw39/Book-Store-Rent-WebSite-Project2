document.addEventListener('DOMContentLoaded', function() {
    fetch('/books')
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById('bookTable').getElementsByTagName('tbody')[0];
            data.forEach(book => {
                const row = document.createElement('tr');

                const titleCell = document.createElement('td');
                titleCell.textContent = book.title;
                row.appendChild(titleCell);

                const priceCell = document.createElement('td');
                priceCell.textContent = book.price;
                row.appendChild(priceCell);

                const pageCell = document.createElement('td');
                pageCell.textContent = book.page;
                row.appendChild(pageCell);

                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error('Error fetching book data:', error));
});