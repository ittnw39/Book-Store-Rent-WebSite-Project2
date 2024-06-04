document.addEventListener("DOMContentLoaded", function () {
    const categoryList = document.getElementById('category-list');
    const addCategoryBtn = document.getElementById('add-category-btn');
    const deleteSelectedBtn = document.getElementById('delete-selected-btn');
    const deleteAllBtn = document.getElementById('delete-all-btn');

    async function fetchCategories() {
        const response = await fetch('/admin/category');
        const categories = await response.json();
        categoryList.innerHTML = '';
        categories.forEach(category => {
            const categoryItem = document.createElement('div');
            categoryItem.className = 'category-item';
            categoryItem.innerHTML = `
                <input type="text" value="${category.name}" data-id="${category.id}">
                <input type="checkbox" data-id="${category.id}">
                <button class="update-btn" data-id="${category.id}">수정</button>
            `;
            categoryList.appendChild(categoryItem);
        });
    }

    async function addCategory() {
        const name = prompt('새 카테고리 이름을 입력하세요:');
        if (name) {
            await fetch('/admin/category', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name })
            });
            fetchCategories();
        }
    }

    async function updateCategory(id, name) {
        await fetch(`/admin/category/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name })
        });
        fetchCategories();
    }

    async function deleteSelectedCategories() {
        const checkboxes = document.querySelectorAll('.category-item input[type="checkbox"]:checked');
        const ids = Array.from(checkboxes).map(checkbox => checkbox.dataset.id);
        await fetch('/admin/category', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(ids)
        });
        fetchCategories();
    }

    async function deleteAllCategories() {
        await fetch('/admin/category', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify([])
        });
        fetchCategories();
    }

    categoryList.addEventListener('click', function (e) {
        if (e.target.classList.contains('update-btn')) {
            const id = e.target.dataset.id;
            const name = e.target.previousElementSibling.previousElementSibling.value;
            updateCategory(id, name);
        }
    });

    addCategoryBtn.addEventListener('click', addCategory);
    deleteSelectedBtn.addEventListener('click', deleteSelectedCategories);
    deleteAllBtn.addEventListener('click', deleteAllCategories);

    fetchCategories();
});
