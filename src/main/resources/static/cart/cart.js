import { checkLogin } from "../useful-functions.js";

checkLogin();

document.addEventListener('DOMContentLoaded', function() {
    fetchCartItems();

    const allSelectCheckbox = document.getElementById('allSelectCheckbox');
    const partialDeleteButton = document.getElementById('partialDeleteLabel');

    // 전체 선택 체크박스 이벤트 리스너
    allSelectCheckbox.addEventListener('change', function() {
        const checkboxes = document.querySelectorAll('.productCheckbox');
        checkboxes.forEach(checkbox => {
            checkbox.checked = allSelectCheckbox.checked;
        });
    });

    // 선택 삭제 버튼 클릭 이벤트 리스너
    partialDeleteButton.addEventListener('click', async () => {
        const checkboxes = document.querySelectorAll('.productCheckbox:checked');
        const itemIds = Array.from(checkboxes).map(checkbox => {
            const cartItem = checkbox.closest('.cart-item');
            if (cartItem) {
                return cartItem.dataset.cartItemId;
            }
            return null;
        }).filter(itemId => itemId !== null);

        if (itemIds.length === 0) {
            alert('삭제할 상품을 선택해주세요.');
            return;
        }

        const confirmed = confirm(`선택한 ${itemIds.length}개의 상품을 삭제하시겠습니까?`);
        if (!confirmed) {
            return;
        }

        try {
            const deletePromises = itemIds.map(itemId => deleteCartItem(itemId));
            await Promise.all(deletePromises);
            alert('선택한 상품이 삭제되었습니다.');
            location.reload(); // 페이지 새로고침
        } catch (error) {
            console.error('Error deleting items:', error);
            alert('상품 삭제하는 중 오류가 발생했습니다.');
        }
    });

    // 상품 삭제 Ajax 요청 함수
    async function deleteCartItem(cartItemId) {
        try {
            const response = await fetch(`/cart/item/${cartItemId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('상품 삭제 실패');
            }
        } catch (error) {
            throw error;
        }
    }

    // 장바구니 아이템 불러오기 함수
    function fetchCartItems() {
        fetch('/api/cart')
            .then(response => {
                if (!response.ok) {
                    throw new Error('장바구니 아이템을 불러오는 중 오류가 발생했습니다.');
                }
                return response.json();
            })
            .then(data => {
                const cartItemsContainer = document.getElementById('cartItemsContainer');
                const productsCount = document.getElementById('productsCount');
                const productsTotal = document.getElementById('productsTotal');
                const deliveryFee = document.getElementById('deliveryFee');
                const orderTotal = document.getElementById('orderTotal');

                let totalQuantity = 0;
                let totalPrice = 0;

                cartItemsContainer.innerHTML = ''; // 기존 카트 아이템을 모두 지우고 다시 렌더링

                data.forEach(item => {
                    totalQuantity += item.quantity;
                    totalPrice += item.quantity * item.price;

                    const itemElement = document.createElement('div');
                    itemElement.classList.add('box', 'media', 'cart-item');
                    itemElement.dataset.cartItemId = item.id; // bookDetailId 사용
                    itemElement.innerHTML = `


                    <input type="checkbox" class="productCheckbox">
                        <div class="media-left">
                            <figure class="image is-64x64">
                                <img src="${item.imageURL}" alt="Product image">
                            </figure>
                        </div>
                        <div class="media-content">
                            <div class="content">
                                <label class="checkbox">

                                    <strong>${item.title}</strong><br>
                                    수량: <button class="button is-small quantity-decrease">-</button>
                                    <span class="quantity">${item.quantity}</span>
                                    <button class="button is-small quantity-increase">+</button><br>
                                    가격: ${item.price}원
                                </label>
                            </div>
                        </div>
                        <div class="media-right">
                            <button class="button is-danger is-small delete-button">삭제</button>
                        </div>
                    `;

                    const deleteButton = itemElement.querySelector('.delete-button');
                    deleteButton.addEventListener('click', async () => {
                        const confirmed = confirm(`상품을 삭제하시겠습니까?`);
                        if (!confirmed) {
                            return;
                        }

                        try {
                            await deleteCartItem(item.bookDetailId);
                            alert('상품이 삭제되었습니다.');
                            location.reload(); // 페이지 새로고침
                        } catch (error) {
                            console.error('Error deleting item:', error);
                            alert('상품 삭제 중 오류가 발생했습니다.');
                        }
                    });

                    const quantityDecreaseButton = itemElement.querySelector('.quantity-decrease');
                    const quantityIncreaseButton = itemElement.querySelector('.quantity-increase');
                    const quantityDisplay = itemElement.querySelector('.quantity');

                    quantityDecreaseButton.addEventListener('click', async () => {
                        let newQuantity = parseInt(quantityDisplay.textContent) - 1;
                        if (newQuantity > 0) {
                            try {
                                await updateCartItemQuantity(item.bookDetailId, newQuantity);
                                location.reload(); // 페이지 새로고침
                            } catch (error) {
                                console.error('Error updating item quantity:', error);
                                alert('수량 업데이트 중 오류가 발생했습니다.');
                            }
                        }
                    });

                    quantityIncreaseButton.addEventListener('click', async () => {
                        let newQuantity = parseInt(quantityDisplay.textContent) + 1;
                        try {
                            await updateCartItemQuantity(item.bookDetailId, newQuantity);
                            location.reload(); // 페이지 새로고침
                        } catch (error) {
                            console.error('Error updating item quantity:', error);
                            alert('수량 업데이트 중 오류가 발생했습니다.');
                        }
                    });

                    cartItemsContainer.appendChild(itemElement);
                });

                productsCount.textContent = `${totalQuantity}개`;
                productsTotal.textContent = `₩${totalPrice.toLocaleString()}원`;
                deliveryFee.textContent = `3000원`;
                orderTotal.textContent = `${(totalPrice + 3000).toLocaleString()}원`;
            })
            .catch(error => {
                console.error('Error fetching cart items:', error);
                alert('장바구니 아이템을 불러오는 중 오류가 발생했습니다.');
            });
    }

    // 수량 업데이트 Ajax 요청 함수
    async function updateCartItemQuantity(cartItemId, quantity) {
        try {
            const response = await fetch(`/cart/item/${cartItemId}?quantity=${quantity}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                }
            });

            if (!response.ok) {
                throw new Error('수량 업데이트 실패');
            }
        } catch (error) {
            throw error;
        }
    }

        // "상품 더 추가하기" 버튼 클릭 이벤트
            const addMoreButton = document.getElementById('addMoreButton');
            addMoreButton.addEventListener('click', () => {
                window.location.href = '/books'; // 상품 리스트 페이지로 이동 (URL은 실제 페이지 경로에 맞게 수정)
            });

            fetchCartItems();


    });
    document.getElementById("purchaseButton").addEventListener("click", function() {
                    window.location.href = "/orders";
                });

    //주문하기 버튼 클릭 이벤트
    document.getElementById("purchaseButton").addEventListener("click", function() {
        window.location.href = "/orders";
    });

