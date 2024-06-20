import { checkAdmin } from "../useful-functions.js";

checkAdmin();

document.addEventListener('DOMContentLoaded', () => {
    const categoryList = document.getElementById('categoryList');
    const categoryForm = document.getElementById('categoryForm');
    const categoryIdInput = document.getElementById('categoryId');
    const categoryNameInput = document.getElementById('categoryName');
    const deleteSelectedButton = document.getElementById('deleteSelected');
    const errorMessage = document.getElementById('errorMessage');

    // Fetch all categories and display them
    function fetchCategories() {

        fetch('/admin/categories', {
            headers: {
                'Accept': 'application/json',
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
                            <span><small>수정</small>${category.updatedAt}</span>
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

        errorMessage.innerText = '';

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name: categoryName })
        }).then(response => {
            if (!response.ok) {
                return response.json().then(error => {
                    throw new Error(error.message || '동일한 카테고리명입니다. 다시 입력해주세요.');
                });
            }
            return response.json();
        }).then(() => {
            categoryForm.reset();
            categoryIdInput.value = ''; // Reset the hidden input value
            errorMessage.innerText = '';
            fetchCategories(); // 변경된 데이터를 반영하기 위해 fetchCategories 호출
        }).catch(error => {
            errorMessage.innerText = error.message || '카테고리를 저장하는 동안 오류가 발생했습니다.';
        });
    });

    // Edit category
    window.editCategory = (id, name) => {
        categoryIdInput.value = id;
        categoryNameInput.value = name;
    };

    // Delete category
    window.deleteCategory = id => {
        if(confirm('정말로 이 카테고리를 삭제하시겠습니까?')) {
            fetch(`/admin/category/${id}`, { method: 'DELETE' })
                .then(() => fetchCategories()) // 변경된 데이터를 반영하기 위해 fetchCategories 호출
                .catch(error => {
                    console.error('Error deleting category:', error);
                });
        }
    };

    // Delete selected categories
    deleteSelectedButton.addEventListener('click', () => {
        const selectedIds = Array.from(document.querySelectorAll('.categoryCheckbox:checked'))
            .map(checkbox => checkbox.getAttribute('data-id'));
        if(confirm('정말로 선택한 카테고리들을 삭제하시겠습니까?')) {
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
        }
    });

    categoryNameInput.addEventListener('input', () => {
        errorMessage.innerText = '';
    })

    // Initial fetch of categories
    fetchCategories();
});
