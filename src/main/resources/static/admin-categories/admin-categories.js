document.addEventListener('DOMContentLoaded', () => {
    const categoryList = document.getElementById('categoryList');
    const categoryForm = document.getElementById('categoryForm');
    const categoryIdInput = document.getElementById('categoryId');
    const categoryNameInput = document.getElementById('categoryName');
    const deleteSelectedButton = document.getElementById('deleteSelected');

    // Fetch all categories and display them
    function fetchCategories() {
        fetch('/admin/category/all', {
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => response.json())
            .then(categories => {
                if (Array.isArray(categories)) {
                    categoryList.innerHTML = '<ul>' + categories.map(category => `
                        <li>
                            <input type="checkbox" class="categoryCheckbox" data-id="${category.id}">
                            <span>${category.name}</span>
                            <span><small>생성</small>${category.createdAt}</span>
                            <span class="updatedAt"><small>수정</small>${category.updatedAt}</span>
                            <button type="button" onclick="editCategory(${category.id}, '${category.name}')">수정</button>
                            <button type="button" onclick="deleteCategory(${category.id})">삭제</button>
                        </li>`).join('') + '</ul>';
                } else {
                    console.error('Unexpected response format:', categories);
                }
            }).catch(error => {
            console.error('Error fetching categories:', error);
        });
    }

    // Add or update category
    categoryForm.addEventListener('submit', event => {
        event.preventDefault();
        const categoryId = categoryIdInput.value;
        const categoryName = categoryNameInput.value;
        const method = categoryId ? 'PUT' : 'POST';
        const url = categoryId ? `/admin/category/${categoryId}` : '/admin/category';

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name: categoryName })
        }).then(() => {
            categoryForm.reset();
            categoryIdInput.value = ''; // Reset the hidden input value
            fetchCategories(); // 변경된 데이터를 반영하기 위해 fetchCategories 호출
        }).catch(error => {
            console.error('Error saving category:', error);
        });
    });

    // Edit category
    window.editCategory = (id, name) => {
        categoryIdInput.value = id;
        categoryNameInput.value = name;
    };

    // Delete category
    window.deleteCategory = id => {
        fetch(`/admin/category/${id}`, { method: 'DELETE' })
            .then(() => fetchCategories()) // 변경된 데이터를 반영하기 위해 fetchCategories 호출
            .catch(error => {
                console.error('Error deleting category:', error);
            });
    };

    // Delete selected categories
    deleteSelectedButton.addEventListener('click', () => {
        const selectedIds = Array.from(document.querySelectorAll('.categoryCheckbox:checked'))
            .map(checkbox => checkbox.getAttribute('data-id'));
        fetch('/admin/category', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(selectedIds)
        }).then(() => fetchCategories()) // 변경된 데이터를 반영하기 위해 fetchCategories 호출
            .catch(error => {
                console.error('Error deleting selected categories:', error);
            });
    });

    // Initial fetch of categories
    fetchCategories();
});
