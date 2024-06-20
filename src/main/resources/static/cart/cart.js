import { checkLogin } from "../useful-functions.js";

checkLogin();

document.addEventListener('DOMContentLoaded', function() {

    console.log("Document is ready!");

        // 장바구니 아이템 불러오기 함수 호출
        fetchCartItems();
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
                    const itemElement = document.createElement('div');
                    itemElement.classList.add('box', 'media', 'cart-item');
                    itemElement.dataset.cartItemId = item.id;
                    itemElement.dataset.bookDetailId = item.bookDetailId; // bookDetailId를 데이터셋에 추가
                    itemElement.dataset.price = item.price.toString();
                    itemElement.dataset.quantity = item.quantity; // quantity를 데이터셋에 추가

                    // 상품 선택 체크박스
                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.classList.add('productCheckbox');
                    itemElement.appendChild(checkbox);

                    // 상품 이미지
                    const mediaLeft = document.createElement('div');
                    mediaLeft.classList.add('media-left');
                    const image = document.createElement('figure');
                    image.classList.add('image', 'is-64x64');
                    const img = document.createElement('img');
                    img.src = item.imageURL;
                    img.alt = 'Product image';
                    image.appendChild(img);
                    mediaLeft.appendChild(image);
                    itemElement.appendChild(mediaLeft);

                    // 상품 내용
                    const mediaContent = document.createElement('div');
                    mediaContent.classList.add('media-content');
                    const content = document.createElement('div');
                    content.classList.add('content');
                    const label = document.createElement('label');
                    label.classList.add('checkbox');
                    const strong = document.createElement('strong');
                    strong.textContent = item.title;
                    label.appendChild(strong);
                    label.appendChild(document.createElement('br'));
                    label.appendChild(document.createTextNode('수량: '));

                    // 수량 감소 버튼
                    const decreaseButton = document.createElement('button');
                    decreaseButton.classList.add('button', 'is-small', 'quantity-decrease');
                    decreaseButton.textContent = '-';
                    label.appendChild(decreaseButton);

                    // 수량 표시
                    const quantitySpan = document.createElement('span');
                    quantitySpan.classList.add('quantity');
                    quantitySpan.textContent = item.quantity;
                    label.appendChild(quantitySpan);

                    // 수량 증가 버튼
                    const increaseButton = document.createElement('button');
                    increaseButton.classList.add('button', 'is-small', 'quantity-increase');
                    increaseButton.textContent = '+';
                    label.appendChild(increaseButton);

                    label.appendChild(document.createElement('br'));
                    label.appendChild(document.createTextNode(`가격: ${item.price}원`));
                    content.appendChild(label);
                    mediaContent.appendChild(content);
                    itemElement.appendChild(mediaContent);

                    // 삭제 버튼
                    const mediaRight = document.createElement('div');
                    mediaRight.classList.add('media-right');
                    const deleteButton = document.createElement('button');
                    deleteButton.classList.add('button', 'is-danger', 'is-small', 'delete-button');
                    deleteButton.textContent = '삭제';
                    mediaRight.appendChild(deleteButton);
                    itemElement.appendChild(mediaRight);

                    cartItemsContainer.appendChild(itemElement);

                    // 체크박스 변경 시 이벤트 리스너 추가
                    checkbox.addEventListener('change', () => {
                        if (checkbox.checked) {
                            totalQuantity += item.quantity;
                            totalPrice += item.quantity * item.price;
                        } else {
                            totalQuantity -= item.quantity;
                            totalPrice -= item.quantity * item.price;
                        }
                        updateCartSummary();
                    });

                    // 수량 감소 버튼 이벤트 리스너
                    decreaseButton.addEventListener('click', async () => {
                        let newQuantity = parseInt(quantitySpan.textContent) - 1;
                        if (newQuantity > 0) {
                            try {
                                await updateCartItemQuantity(item.id, newQuantity);
                                quantitySpan.textContent = newQuantity;
                                if (checkbox.checked) {
                                    totalQuantity -= 1;
                                    totalPrice -= item.price;
                                    updateCartSummary();
                                }
                            } catch (error) {
                                console.error('Error updating item quantity:', error);
                                alert('수량 업데이트 중 오류가 발생했습니다.');
                            }
                        }
                    });

                    // 수량 증가 버튼 이벤트 리스너
                    increaseButton.addEventListener('click', async () => {
                        let newQuantity = parseInt(quantitySpan.textContent) + 1;
                        try {
                            await updateCartItemQuantity(item.id, newQuantity);
                            quantitySpan.textContent = newQuantity;
                            if (checkbox.checked) {
                                totalQuantity += 1;
                                totalPrice += item.price;
                                updateCartSummary();
                            }
                        } catch (error) {
                            console.error('Error updating item quantity:', error);
                            alert('수량 업데이트 중 오류가 발생했습니다.');
                        }
                    });

                    // 삭제 버튼 이벤트 리스너
                    deleteButton.addEventListener('click', async () => {
                        const confirmed = confirm(`상품을 삭제하시겠습니까?`);
                        if (!confirmed) {
                            return;
                        }

                        try {
                            await deleteCartItem(item.id);
                            alert('상품이 삭제되었습니다.');
                            fetchCartItems();
                        } catch (error) {
                            console.error('Error deleting item:', error);
                            alert('상품 삭제 중 오류가 발생했습니다.');
                        }
                    });
                    console.log(`Item ID ${item.id}, Book Detail ID: ${item.bookDetailId}, Price: ${item.price}원`);
                });

                // 상품 수량 및 가격 업데이트 함수
                function updateCartSummary() {
                    productsCount.textContent = `${totalQuantity}개`;
                    productsTotal.textContent = `₩${totalPrice.toLocaleString()}원`;
                    deliveryFee.textContent = `₩3,000원`;
                    orderTotal.textContent = `₩${(totalPrice + 3000).toLocaleString()}원`;
                }

                // 초기 로드 시 전체 선택 체크박스 이벤트 리스너 추가
                const allSelectCheckbox = document.getElementById('allSelectCheckbox');
                allSelectCheckbox.addEventListener('change', function() {
                    const checkboxes = document.querySelectorAll('.productCheckbox');
                    checkboxes.forEach(checkbox => {
                        checkbox.checked = allSelectCheckbox.checked;
                        if (checkbox.checked) {
                            const item = data.find(item => item.id === parseInt(checkbox.closest('.cart-item').dataset.cartItemId));
                            totalQuantity += item.quantity;
                            totalPrice += item.quantity * item.price;
                        } else {
                            const item = data.find(item => item.id === parseInt(checkbox.closest('.cart-item').dataset.cartItemId));
                            totalQuantity -= item.quantity;
                            totalPrice -= item.quantity * item.price;
                        }
                    });
                    updateCartSummary();
                });

                // 초기 로드 시 선택 삭제 버튼 이벤트 리스너 추가
                const partialDeleteButton = document.getElementById('partialDeleteLabel');
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
                        fetchCartItems();
                    } catch (error) {
                        console.error('Error deleting items:', error);
                        alert('상품 삭제하는 중 오류가 발생했습니다.');
                    }
                });

                // 초기 로드 시 주문하기 버튼 이벤트 리스너 추가
                document.getElementById("purchaseButton").addEventListener("click", function() {
                    const selectedItems = [];

                    document.querySelectorAll('.productCheckbox:checked').forEach(checkbox => {
                        const cartItemElement = checkbox.closest('.cart-item');
                        if (cartItemElement) {
                            const itemId = parseInt(cartItemElement.dataset.cartItemId);
                            const bookDetailId = parseInt(cartItemElement.dataset.bookDetailId);
                            const titleElement = cartItemElement.querySelector('.content strong');
                            const quantityElement = cartItemElement.querySelector('.quantity');
                            const price = parseInt(cartItemElement.dataset.price);

                            // null 체크 후 데이터 추출
                            const title = titleElement ? titleElement.innerText : 'Unknown';
                            const quantity = quantityElement ? parseInt(quantityElement.innerText) : 0;

                            selectedItems.push({
                                id: itemId,
                                bookDetailId: bookDetailId,
                                title: title,
                                quantity: quantity,
                                price: price



                            });
                        }
                    });

                    // 선택된 항목 데이터를 콘솔에 출력하여 확인
                    console.log('Selected items in cart.js:', selectedItems);

                    if (selectedItems.length === 0) {
                        alert('주문할 상품을 선택해주세요.');
                        return;
                    }

                    // 선택된 상품 데이터를 로컬 스토리지에 저장
                    localStorage.setItem('selectedItems', JSON.stringify(selectedItems));

                    // 주문 요약 페이지로 이동
                   window.location.href = '/orders';
                });

                // 초기 로드 시 장바구니 아이템 수량 및 가격 업데이트
                updateCartSummary();
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



    // 초기 로드 시 장바구니 아이템 로드
    fetchCartItems();
});

//상품 더 추가하기
document.getElementById('addMoreButton').addEventListener('click', function() {
    window.location.href = '/books';
    });