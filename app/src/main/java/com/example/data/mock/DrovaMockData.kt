package com.example.data.mock

import com.example.domain.model.*

object DrovaMockData {

    val sampleAddresses = listOf(
        SavedAddress(
            id = "addr_1",
            labelAr = "المنزل",
            labelEn = "Home",
            districtAr = "6 أكتوبر، 6 أكتوبر",
            detailedAddressAr = "عمارة 14، شارع النصر، الدور 3، شقة 6",
            isDefault = true
        ),
        SavedAddress(
            id = "addr_2",
            labelAr = "العمل",
            labelEn = "Work",
            districtAr = "الشيخ زايد، الشيخ زايد",
            detailedAddressAr = "مبنى الأعمال 3B، شارع التسعين الشيخ زايد�، الدور 2",
            isDefault = false
        ),
        SavedAddress(
            id = "addr_3",
            labelAr = "بيت العائلة",
            labelEn = "Family",
            districtAr = "حدائق أكتوبر، الجيزة",
            detailedAddressAr = "شارع مصدق، عمارة الأطباء، الدور 5",
            isDefault = false
        )
    )

    val sampleRestaurants = listOf(
        Restaurant(
            id = "rest_1",
            nameAr = "شاورما DROVA 6 أكتوبر",
            nameEn = "Al Reem Shawarma",
            categoryAr = "شاورما ومشويات",
            categoryEn = "Shawarma & Grills",
            rating = 4.8,
            reviewCount = 1420,
            deliveryTimeMin = 25,
            minOrderEgp = 60.0,
            deliveryFeeEgp = 20.0,
            isOpen = true,
            addressAr = "محور 26 يوليو، 6 أكتوبر",
            commissionRatePercent = 12.0,
            activeSubscriptionTier = "DROVA Pro Partner",
            menu = listOf(
                MenuItem("m1", "ساندوتش شاورما فراخ عربي عائلي", "Arabic Chicken Shawarma Box", "شاورما دجاج متبلة بالخلطة السورية مع صوص تومية كريمي وبطاطس مبهرة ومخلل خيار بيتي في خبز صاج محمص", 145.0, "شاورما", preparationTimeMin = 15),
                MenuItem("m2", "ساندوتش شاورما لحم بلدي دوبل", "Double Beef Shawarma", "شرائح لحم بقري متبل مع طحينة بيضاء فاخرة وبقدونس طازج وبصل متبل بالسماق في خبز كايزر طازج", 165.0, "شاورما", preparationTimeMin = 15),
                MenuItem("m3", "وجبة فتة شاورما مكس الريم", "Mixed Shawarma Fattah", "أرز بسمتي أصفر فاخر مع شرائح شاورما دجاج ولحم وخبز شامي مقرمش وصوص الثومية المميز واللوز المحمص", 180.0, "وجبات", preparationTimeMin = 20),
                MenuItem("m4", "نصف دجاجة شواية مع أرز وبطاطس", "Half Grilled Chicken Meal", "نصف دجاجة متبلة ومشوية على الفحم مع أرز بسمتي بالخلطة وصوص دقوس وثومية وخبز صاج", 155.0, "وجبات", preparationTimeMin = 25),
                MenuItem("m5", "بطاطس محمرة مبهرة مع صوص جبنة", "Spiced Fries with Cheese", "أصابع بطاطس مقرمشة متبلة بتوابل الريم الخاصة مع صوص جبنة شيدر دافئة", 45.0, "مقبلات", preparationTimeMin = 10),
                MenuItem("m6", "سلطة ثومية حارة أصلية", "Spicy Garlic Dip", "صوص ثومية سوري متبل بالشطة الحلبية وزيت الزيتون البكر", 25.0, "مقبلات", preparationTimeMin = 5)
            )
        ),
        Restaurant(
            id = "rest_2",
            nameAr = "برجر ستايشن الشيخ زايد",
            nameEn = "Burger Box New Cairo",
            categoryAr = "برجر وسريع",
            categoryEn = "Burgers & Fast Food",
            rating = 4.9,
            reviewCount = 2100,
            deliveryTimeMin = 30,
            minOrderEgp = 80.0,
            deliveryFeeEgp = 25.0,
            isOpen = true,
            addressAr = "الحي السابع، 6 أكتوبر",
            commissionRatePercent = 10.0,
            activeSubscriptionTier = "DROVA Pro Partner",
            menu = listOf(
                MenuItem("m7", "سموكد بيكون تريبل برجر", "Smoked Bacon Triple Burger", "3 قطع لحم بقري أنجوس طازج 150جم مع جبنة شيدر مدخنة وبيكون مقرمش وصوص باربيكيو مدخن في خبز بريوش", 240.0, "برجر", preparationTimeMin = 18),
                MenuItem("m8", "ترافل كريسبي تشيكن سوبريم", "Truffle Crispy Chicken", "صدر دجاج مقرمش ذهبي مع مايونيز الترافل الأسود وجبنة سويسرية وخس طازج وطماطم", 195.0, "دجاج", preparationTimeMin = 15),
                MenuItem("m9", "أوريجينال دبل سماش برجر", "Original Double Smash", "قطعتين لحم سماش محمر مع جبنة أمريكية ذائبة ومخلل وبصل مكرمل وصوص بوكس السري", 175.0, "برجر", preparationTimeMin = 14),
                MenuItem("m10", "حلقات بصل مقرمشة مع صوص رانش", "Crispy Onion Rings", "حلقات بصل طازجة مقلية ذهبية مع صوص الرانش بالأعشاب", 50.0, "مقبلات", preparationTimeMin = 8),
                MenuItem("m11", "تشيز فرايز هالبينو لودد", "Jalapeno Loaded Cheese Fries", "بطاطس محمرة مغطاة بصوص الجبن الذائب وقطع الهالبينو الحار وفتات البيكون", 65.0, "مقبلات", preparationTimeMin = 10)
            )
        ),
        Restaurant(
            id = "rest_3",
            nameAr = "كشري DROVA حدائق أكتوبر",
            nameEn = "Koshary El Tahrir",
            categoryAr = "مأكولات شعبية",
            categoryEn = "Traditional Egyptian",
            rating = 4.7,
            reviewCount = 3800,
            deliveryTimeMin = 20,
            minOrderEgp = 40.0,
            deliveryFeeEgp = 15.0,
            isOpen = true,
            addressAr = "الشيخ زايد، الجيزة",
            commissionRatePercent = 14.0,
            activeSubscriptionTier = "DROVA Basic Partner",
            menu = listOf(
                MenuItem("m12", "علبة كشري التحرير سوبر فويل", "Super Koshary Box", "خلطة أرز بالشعرية مع عدس أسود ومكرونة متنوعة وحمص شامي وصلصة طماطم مسبكة ودقة وشطة وبصل مقرمش ذهبي", 65.0, "كشري", preparationTimeMin = 10),
                MenuItem("m13", "علبة كشري جامبو عائلي", "Jumbo Family Koshary", "علبة كشري حجم كبير جداً تكفي شخصين مع أكياس صلصة إضافية ودقة ليمون بالثوم وتقلية مقرمشة", 95.0, "كشري", preparationTimeMin = 10),
                MenuItem("m14", "طاجن مكرونة باللحم المفروم البلدي", "Baked Pasta with Minced Beef", "طاجن فرن فخاري مع مكرونة فرن ولحم مفروم متبل بالبهارات المصرية وصلصة طماطم مسبكة", 85.0, "طواجن", preparationTimeMin = 15),
                MenuItem("m15", "طاجن مكرونة بالفراخ والموزاريلا", "Chicken Mozzarella Pasta Casserole", "مكرونة فرن بقطع صدور الدجاج المتبلة مع جبنة موزاريلا سايحة على الوجه", 90.0, "طواجن", preparationTimeMin = 15),
                MenuItem("m16", "أرز باللبن فرن ومكسرات فاخرة", "Baked Rice Pudding with Nuts", "أرز باللبن بلدي مطهو في الفرن مع قشطة بلدي وفستق ولوز وزبيب", 35.0, "حلويات", preparationTimeMin = 5),
                MenuItem("m17", "مهلبية قمر الدين بالمكسرات", "Qamar Al-Din Pudding", "مهلبية مشمش طبيعية منعشة مزينة بالمكسرات وجوز الهند", 30.0, "حلويات", preparationTimeMin = 5)
            )
        ),
        Restaurant(
            id = "rest_4",
            nameAr = "بيتزا زايد الإيطالية",
            nameEn = "Pizza & Capri Zamalek",
            categoryAr = "بيتزا وإيطالي",
            categoryEn = "Italian Pizza & Pasta",
            rating = 4.8,
            reviewCount = 950,
            deliveryTimeMin = 35,
            minOrderEgp = 120.0,
            deliveryFeeEgp = 25.0,
            isOpen = true,
            addressAr = "الشيخ زايد، الجيزة",
            commissionRatePercent = 11.0,
            activeSubscriptionTier = "DROVA Pro Partner",
            menu = listOf(
                MenuItem("m18", "بيتزا مارجريتا نابوليتانا كلاسيك", "Neapolitan Margherita", "عجينة إيطالية مخمرة 48 ساعة مع صلصة طماطم سان مارزانو وجبنة موتزاريلا دي بوفالا وأوراق ريحان طازجة وزيت زيتون بكر", 190.0, "بيتزا", preparationTimeMin = 20),
                MenuItem("m19", "بيتزا كواترو فورماجي 4 أجبان", "Quattro Formaggi Pizza", "مزيج غني من 4 أجبان إيطالية فاخرة: موتزاريلا، جورجونزولا، بارميزان، وجودا مع صوص أبيض", 230.0, "بيتزا", preparationTimeMin = 20),
                MenuItem("m20", "باستا بيني ألفريدو دجاج ومشروم", "Penne Alfredo Chicken", "مكرونة بيني مع صوص كريمي فاخر، صدور دجاج مشوية، فطر طازج، وجبنة بارميزان معتقة", 215.0, "باستا", preparationTimeMin = 20),
                MenuItem("m21", "لازانيا بولونيز بالبشاميل الغني", "Classic Beef Lasagna", "طبقات مكرونة لازانيا مع لحم مفروم بالصوص الإيطالي وبشاميل كريمي وموتزاريلا ذهبية محمرة", 220.0, "باستا", preparationTimeMin = 25),
                MenuItem("m22", "تيراميسو ماسكاربوني إيطالي", "Classic Italian Tiramisu", "حلوى التيراميسو الإيطالية الأصلية مع بسكويت السافوياردي المنقوع في الإسبريسو وجبنة الماسكاربوني", 85.0, "حلويات", preparationTimeMin = 5)
            )
        ),
        Restaurant(
            id = "rest_5",
            nameAr = "قصر الكبابجي 6 أكتوبر",
            nameEn = "Kaser El Kababgy",
            categoryAr = "شاورما ومشويات",
            categoryEn = "Egyptian Grills & BBQ",
            rating = 4.9,
            reviewCount = 4200,
            deliveryTimeMin = 40,
            minOrderEgp = 150.0,
            deliveryFeeEgp = 30.0,
            isOpen = true,
            addressAr = "منطقة المطاعم، حدائق أكتوبر",
            commissionRatePercent = 10.0,
            activeSubscriptionTier = "DROVA Pro Partner",
            menu = listOf(
                MenuItem("m23", "كيلو مشكل كباب وكفتة ضاني", "1Kg Mixed Kebab & Kofta", "كباب بتلو متبل وكفتة لحم ضاني مشوية على الفحم مع خضار مشوي، خبز بلدي ساخن، طحينة وسلطة خضراء", 680.0, "مشويات", preparationTimeMin = 30),
                MenuItem("m24", "نصف كيلو ريش ضاني بلدي مشوية", "500g Grilled Lamb Chops", "ريش ضاني متبلة بخلطة الكبابجي السرية ومشوية بعناية مع أرز بسمتي بالخلطة والمكسرات", 490.0, "مشويات", preparationTimeMin = 30),
                MenuItem("m25", "طاجن عكاوي بالبصل القاورما", "Oxtail Clay Pot with Onions", "لحم عكاوي بلدي مطهو ببطء في الفرن الفخاري مع البصل المكرمل وتوابل القصر الغنية", 295.0, "طواجن", preparationTimeMin = 35),
                MenuItem("m26", "حمام محشي أرز بالكبد والقوانص (زوج)", "Stuffed Pigeons with Rice (Pair)", "زوج حمام بلدي محشي أرز مصري متبل بالكبد والمكسرات ومحمر بالسمن البلدي", 260.0, "أطباق خاصة", preparationTimeMin = 30),
                MenuItem("m27", "شوربة لسان عصفور بلدي", "Orzo Soup", "شوربة مرقة لحم غنية مع لسان عصفور محمر وليمون", 40.0, "شوربة ومقبلات", preparationTimeMin = 10)
            )
        ),
        Restaurant(
            id = "rest_6",
            nameAr = "بلبن للحلويات والقشطوطة",
            nameEn = "B.Laban Desserts",
            categoryAr = "حلويات ومخابز",
            categoryEn = "Desserts & Sweets",
            rating = 4.9,
            reviewCount = 5600,
            deliveryTimeMin = 25,
            minOrderEgp = 50.0,
            deliveryFeeEgp = 20.0,
            isOpen = true,
            addressAr = "حدائق أكتوبر، الجيزة",
            commissionRatePercent = 12.0,
            activeSubscriptionTier = "DROVA Pro Partner",
            menu = listOf(
                MenuItem("m28", "قشطوطة لوتس بالسمنة البلدي", "Lotus Qashtouta Cake", "كيكة حليب مسقية غنية بالقشطة البلدي ومغطاة بكريمة وبسكويت اللوتس المقرمش", 85.0, "قشطوطة", preparationTimeMin = 10),
                MenuItem("m29", "قشطوطة مانجو كيت طازجة", "Fresh Mango Qashtouta", "كيكة قشطوطة بالحليب والقشطة مع قطع مانجو كيت طازجة وفيرة", 95.0, "قشطوطة", preparationTimeMin = 10),
                MenuItem("m30", "أم علي بالقشطة والمكسرات البلدي", "Om Ali with Cream & Nuts", "رقائق ميل فوي بالحليب الساخن مع قشطة بلدي وفستق وبندق ولوز وزبيب في طاجن فخار", 75.0, "أطباق ساخنة", preparationTimeMin = 15),
                MenuItem("m31", "كشري حلو نوتيلا ومكسرات بلبن", "Sweet Koshary Nutella", "ابتكار بلبن الشهير: كنافة وجلاش وأرز باللبن مع نوتيلا وفستق وبسكويت", 90.0, "ابتكارات بلبن", preparationTimeMin = 10),
                MenuItem("m32", "أيس كريم بلبن فستق حلبي طبيعي", "Pistachio Gelato Cup", "أيس كريم حليب طبيعي غني بقطع وزبدة الفستق الحلبي الأصلي", 60.0, "مثلجات", preparationTimeMin = 5)
            )
        ),
        Restaurant(
            id = "rest_7",
            nameAr = "أسماك قنال السويس",
            nameEn = "Suez Canal Seafood",
            categoryAr = "أسماك وبحرية",
            categoryEn = "Seafood & Fish",
            rating = 4.7,
            reviewCount = 1150,
            deliveryTimeMin = 45,
            minOrderEgp = 140.0,
            deliveryFeeEgp = 25.0,
            isOpen = true,
            addressAr = "6 أكتوبر، الجيزة",
            commissionRatePercent = 12.0,
            activeSubscriptionTier = "DROVA Basic Partner",
            menu = listOf(
                MenuItem("m33", "كيلو جمبري جامبو مشوي زبدة وتوم", "1Kg Grilled Jumbo Shrimp", "جمبري بحري طازج مشوي بصوص الزبدة والتوم والأعشاب مع أرز صيادية وسلطات", 550.0, "جمبري", preparationTimeMin = 25),
                MenuItem("m34", "سمك قاروص سنجاري بالخلطة الإسكندراني", "Sea Bass Singari", "سمكة قاروص طازجة مفتوحة سنجاري بالفرن مع طماطم، فلفل، توم، زيت وليمون", 320.0, "أسماك", preparationTimeMin = 30),
                MenuItem("m35", "طاجن فواكه البحر بالكريمة والموزاريلا", "Seafood Casserole with Cream", "طاجن جمبري وسبيط وكابوريا وفيليه بصوص الكريمة والجبنة الموزاريلا", 280.0, "طواجن بحرية", preparationTimeMin = 30),
                MenuItem("m36", "شوربة سي فود مخلية بالكريمة", "Creamy Seafood Soup", "شوربة مرقة جمبري بالكريمة غنية بقطع الجمبري والسبيط وفيليه السمك", 95.0, "شوربة", preparationTimeMin = 15)
            )
        )
    )

    val sampleOrders = listOf(
        Order(
            id = "ord_105",
            orderNumber = "DRV-9012",
            customerId = "cust_2",
            customerName = "سارة إبراهيم",
            customerPhone = "+201023456789",
            deliveryAddressAr = "برج زهرة 6 أكتوبر، كورنيش النيل، الدور 7",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            captainId = null,
            captainName = null,
            captainPhone = null,
            items = listOf(
                OrderItem("i1", "ساندوتش شاورما فراخ عربي عائلي", "Arabic Chicken Shawarma Box", 2, 145.0, "تومية ومخلل إضافي"),
                OrderItem("i6", "سلطة ثومية حارة أصلية", "Spicy Garlic Dip", 2, 25.0)
            ),
            subtotalEgp = 340.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            totalEgp = 365.0,
            status = OrderStatus.CREATED,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            createdAtFormatted = "الآن، منذ دقيقتين",
            estimatedArrivalMin = 25,
            specialInstructions = "يرجى تسليم الطلب عند الباب الخارجي للبرج"
        ),
        Order(
            id = "ord_106",
            orderNumber = "DRV-9014",
            customerId = "cust_3",
            customerName = "طارق النجار",
            customerPhone = "+201145678901",
            deliveryAddressAr = "ميدان الحرية، 6 أكتوبر، عمارة 8 شقة 3",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            captainId = null,
            captainName = null,
            captainPhone = null,
            items = listOf(
                OrderItem("i3", "وجبة فتة شاورما مكس الريم", "Mixed Shawarma Fattah", 1, 180.0, "زيادة لوز وصوص ثومية"),
                OrderItem("i5", "بطاطس محمرة مبهرة مع صوص جبنة", "Spiced Fries with Cheese", 1, 45.0)
            ),
            subtotalEgp = 225.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            totalEgp = 250.0,
            status = OrderStatus.RESTAURANT_CONFIRMED,
            paymentMethod = PaymentMethod.WALLET,
            createdAtFormatted = "اليوم، منذ 6 دقائق",
            estimatedArrivalMin = 22,
            specialInstructions = "الاتصال عند الوصول"
        ),
        Order(
            id = "ord_107",
            orderNumber = "DRV-9015",
            customerId = "cust_4",
            customerName = "عمر حسام",
            customerPhone = "+201234567890",
            deliveryAddressAr = "شارع النادي، دجلة 6 أكتوبر، فيلا 12",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            captainId = "cap_1",
            captainName = "محمود عادل (كابتن DROVA)",
            captainPhone = "+201198765432",
            items = listOf(
                OrderItem("i2", "ساندوتش شاورما لحم بلدي دوبل", "Double Beef Shawarma", 3, 165.0, "بدون بصل، زيادة طحينة")
            ),
            subtotalEgp = 495.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            totalEgp = 520.0,
            status = OrderStatus.PREPARING,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            createdAtFormatted = "اليوم، منذ 14 دقيقة",
            estimatedArrivalMin = 18,
            specialInstructions = "تسليم للرسبشن"
        ),
        Order(
            id = "ord_108",
            orderNumber = "DRV-9016",
            customerId = "cust_5",
            customerName = "مريم عبد العزيز",
            customerPhone = "+201098765432",
            deliveryAddressAr = "شارع 105، حدائق 6 أكتوبر، عمارة الأمل",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            captainId = "cap_1",
            captainName = "محمود عادل (كابتن DROVA)",
            captainPhone = "+201198765432",
            items = listOf(
                OrderItem("i4", "نصف دجاجة شواية مع أرز وبطاطس", "Half Grilled Chicken Meal", 2, 155.0)
            ),
            subtotalEgp = 310.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            totalEgp = 335.0,
            status = OrderStatus.READY_FOR_PICKUP,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            createdAtFormatted = "اليوم، منذ 20 دقيقة",
            estimatedArrivalMin = 15
        ),
        Order(
            id = "ord_101",
            orderNumber = "DRV-8942",
            customerId = "cust_1",
            customerName = "أحمد مصطفى",
            customerPhone = "+201012345678",
            deliveryAddressAr = "عمارة 14، حدائق أكتوبر، الجيزة",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            captainId = "cap_1",
            captainName = "محمود عادل (كابتن DROVA)",
            captainPhone = "+201198765432",
            items = listOf(
                OrderItem("i1", "ساندوتش شاورما فراخ عربي عائلي", "Arabic Chicken Shawarma Box", 2, 145.0, "زيادة تومية وبطاطس"),
                OrderItem("i5", "بطاطس محمرة مبهرة مع صوص جبنة", "Spiced Fries with Cheese", 1, 45.0)
            ),
            subtotalEgp = 335.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            totalEgp = 360.0,
            status = OrderStatus.ON_THE_WAY,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            createdAtFormatted = "اليوم، 01:15 م",
            estimatedArrivalMin = 12,
            specialInstructions = "يرجى رن جرس الباب والتسليم بالدور الثالث شقة 6"
        ),
        Order(
            id = "ord_109",
            orderNumber = "DRV-8938",
            customerId = "cust_6",
            customerName = "محمد سامي",
            customerPhone = "+201122334455",
            deliveryAddressAr = "شارع اللاسلكي، 6 أكتوبر الجديدة",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            captainId = "cap_1",
            captainName = "محمود عادل",
            captainPhone = "+201198765432",
            items = listOf(
                OrderItem("i1", "ساندوتش شاورما فراخ عربي عائلي", "Arabic Chicken Shawarma Box", 2, 145.0),
                OrderItem("i3", "وجبة فتة شاورما مكس الريم", "Mixed Shawarma Fattah", 1, 180.0)
            ),
            subtotalEgp = 470.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            totalEgp = 495.0,
            status = OrderStatus.COMPLETED,
            paymentMethod = PaymentMethod.WALLET,
            createdAtFormatted = "اليوم، 11:45 ص",
            estimatedArrivalMin = 0
        ),
        Order(
            id = "ord_110",
            orderNumber = "DRV-8930",
            customerId = "cust_7",
            customerName = "هاني خليل",
            customerPhone = "+201066778899",
            deliveryAddressAr = "زهراء 6 أكتوبر، شطر 7، عمارة 21",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            captainId = "cap_1",
            captainName = "محمود عادل",
            captainPhone = "+201198765432",
            items = listOf(
                OrderItem("i2", "ساندوتش شاورما لحم بلدي دوبل", "Double Beef Shawarma", 1, 165.0),
                OrderItem("i4", "نصف دجاجة شواية مع أرز وبطاطس", "Half Grilled Chicken Meal", 1, 155.0)
            ),
            subtotalEgp = 320.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            totalEgp = 345.0,
            status = OrderStatus.COMPLETED,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            createdAtFormatted = "اليوم، 12:30 م",
            estimatedArrivalMin = 0
        ),
        Order(
            id = "ord_102",
            orderNumber = "DRV-8943",
            customerId = "cust_1",
            customerName = "أحمد مصطفى",
            customerPhone = "+201012345678",
            deliveryAddressAr = "عمارة 14، حدائق أكتوبر، الجيزة",
            restaurantId = "rest_2",
            restaurantNameAr = "برجر ستايشن الشيخ زايد",
            restaurantAddressAr = "الحي السابع، 6 أكتوبر",
            captainId = null,
            captainName = null,
            captainPhone = null,
            items = listOf(
                OrderItem("i3", "سموكد بيكون تريبل برجر", "Smoked Bacon Triple Burger", 1, 240.0),
                OrderItem("i4", "حلقات بصل مقرمشة مع صوص رانش", "Crispy Onion Rings", 1, 50.0)
            ),
            subtotalEgp = 290.0,
            deliveryFeeEgp = 25.0,
            platformFeeEgp = 5.0,
            totalEgp = 320.0,
            status = OrderStatus.PREPARING,
            paymentMethod = PaymentMethod.WALLET,
            createdAtFormatted = "اليوم، 01:30 م",
            estimatedArrivalMin = 25,
            specialInstructions = "بدون مايونيز إضافي"
        ),
        Order(
            id = "ord_100",
            orderNumber = "DRV-8940",
            customerId = "cust_1",
            customerName = "أحمد مصطفى",
            customerPhone = "+201012345678",
            deliveryAddressAr = "عمارة 14، حدائق أكتوبر، الجيزة",
            restaurantId = "rest_4",
            restaurantNameAr = "بيتزا زايد الإيطالية",
            restaurantAddressAr = "الشيخ زايد، الجيزة",
            captainId = "cap_1",
            captainName = "محمود عادل",
            captainPhone = "+201198765432",
            items = listOf(
                OrderItem("i7", "بيتزا مارجريتا نابوليتانا كلاسيك", "Neapolitan Margherita", 1, 190.0),
                OrderItem("i8", "تيراميسو ماسكاربوني إيطالي", "Classic Italian Tiramisu", 1, 85.0)
            ),
            subtotalEgp = 275.0,
            deliveryFeeEgp = 25.0,
            platformFeeEgp = 5.0,
            totalEgp = 305.0,
            status = OrderStatus.COMPLETED,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            createdAtFormatted = "أمس، 08:45 م",
            estimatedArrivalMin = 0
        ),
        Order(
            id = "ord_099",
            orderNumber = "DRV-8935",
            customerId = "cust_1",
            customerName = "أحمد مصطفى",
            customerPhone = "+201012345678",
            deliveryAddressAr = "عمارة 14، حدائق أكتوبر، الجيزة",
            restaurantId = "rest_6",
            restaurantNameAr = "بلبن للحلويات والقشطوطة",
            restaurantAddressAr = "حدائق أكتوبر، الجيزة",
            captainId = "cap_1",
            captainName = "محمود عادل",
            captainPhone = "+201198765432",
            items = listOf(
                OrderItem("i9", "قشطوطة لوتس بالسمنة البلدي", "Lotus Qashtouta Cake", 2, 85.0),
                OrderItem("i10", "أم علي بالقشطة والمكسرات البلدي", "Om Ali with Cream & Nuts", 1, 75.0)
            ),
            subtotalEgp = 245.0,
            deliveryFeeEgp = 20.0,
            platformFeeEgp = 5.0,
            totalEgp = 270.0,
            status = OrderStatus.COMPLETED,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            createdAtFormatted = "قبل يومين، 09:15 م",
            estimatedArrivalMin = 0
        )
    )

    val sampleDeliveryTasks = listOf(
        DeliveryTask(
            orderId = "ord_103",
            orderNumber = "DRV-8944",
            restaurantId = "rest_3",
            restaurantNameAr = "كشري DROVA حدائق أكتوبر",
            restaurantNameEn = "Koshary El Tahrir",
            restaurantAddressAr = "الشيخ زايد، الجيزة",
            restaurantPhone = "+201099887766",
            pickupDistanceKm = 1.2,
            customerName = "كريم السيد",
            customerPhone = "+201012345678",
            customerAddressAr = "برج الأطباء، شارع حدائق أكتوبر، الجيزة",
            dropoffDistanceKm = 2.4,
            estimatedTimeMin = 18,
            baseEarningsEgp = 35.0,
            bonusEgp = 7.5,
            estimatedEarningsEgp = 42.50,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            orderTotalEgp = 285.0,
            status = OrderStatus.READY_FOR_PICKUP,
            itemsSummary = "3 علبة كشري سوبر + 2 أرز باللبن",
            itemsList = listOf(
                OrderItem("dt1", "علبة كشري التحرير سوبر فويل", "Super Koshary Box", 3, 65.0, "شطة وصلصة زيادة"),
                OrderItem("dt2", "أرز باللبن فرن ومكسرات فاخرة", "Baked Rice Pudding", 2, 35.0)
            ),
            specialInstructions = "يرجى رن جرس الباب والتسليم بالدور الرابع شقة 8",
            createdAtFormatted = "منذ 3 دقائق"
        ),
        DeliveryTask(
            orderId = "ord_104",
            orderNumber = "DRV-8945",
            restaurantId = "rest_2",
            restaurantNameAr = "برجر ستايشن الشيخ زايد",
            restaurantNameEn = "Burger Box New Cairo",
            restaurantAddressAr = "الحي السابع، 6 أكتوبر",
            restaurantPhone = "+201055443322",
            pickupDistanceKm = 2.8,
            customerName = "مروة طارق",
            customerPhone = "+201122334455",
            customerAddressAr = "شارع الشويفات، الشيخ زايد، فيلا 22",
            dropoffDistanceKm = 3.5,
            estimatedTimeMin = 25,
            baseEarningsEgp = 45.0,
            bonusEgp = 10.0,
            estimatedEarningsEgp = 55.00,
            paymentMethod = PaymentMethod.WALLET,
            orderTotalEgp = 410.0,
            status = OrderStatus.READY_FOR_PICKUP,
            itemsSummary = "2 سموكد بيكون برجر + 1 ترافل تشيكن",
            itemsList = listOf(
                OrderItem("dt3", "سموكد بيكون تريبل برجر", "Smoked Bacon Triple Burger", 1, 240.0),
                OrderItem("dt4", "ترافل كريسبي تشيكن سوبريم", "Truffle Crispy Chicken", 1, 195.0)
            ),
            specialInstructions = "ترك الطلب مع أمن البوابة الرئيسية",
            createdAtFormatted = "منذ 7 دقائق"
        ),
        DeliveryTask(
            orderId = "ord_112",
            orderNumber = "DRV-8950",
            restaurantId = "rest_6",
            restaurantNameAr = "بلبن للحلويات والقشطوطة",
            restaurantNameEn = "B.Laban Desserts",
            restaurantAddressAr = "حدائق أكتوبر، الجيزة",
            restaurantPhone = "+201033221100",
            pickupDistanceKm = 0.9,
            customerName = "أشرف كمال",
            customerPhone = "+201298765432",
            customerAddressAr = "شارع دجلة 206، 6 أكتوبر، عمارة 5",
            dropoffDistanceKm = 1.8,
            estimatedTimeMin = 15,
            baseEarningsEgp = 30.0,
            bonusEgp = 5.0,
            estimatedEarningsEgp = 35.00,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            orderTotalEgp = 175.0,
            status = OrderStatus.READY_FOR_PICKUP,
            itemsSummary = "1 قشطوطة لوتس + 1 أم علي بالمكسرات",
            itemsList = listOf(
                OrderItem("dt5", "قشطوطة لوتس بالسمنة البلدي", "Lotus Qashtouta", 1, 85.0),
                OrderItem("dt6", "أم علي بالقشطة والمكسرات البلدي", "Om Ali with Cream", 1, 75.0)
            ),
            specialInstructions = "الحلويات قابلة للكسر يرجى الحذر أثناء القيادة",
            createdAtFormatted = "الآن"
        )
    )

    val sampleCompletedTasks = listOf(
        DeliveryTask(
            orderId = "ord_109",
            orderNumber = "DRV-8938",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantNameEn = "Al Reem Shawarma",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            restaurantPhone = "+201000000000",
            pickupDistanceKm = 1.5,
            customerName = "محمد سامي",
            customerPhone = "+201122334455",
            customerAddressAr = "شارع اللاسلكي، 6 أكتوبر الجديدة، برج النيل 3",
            dropoffDistanceKm = 2.1,
            estimatedTimeMin = 20,
            baseEarningsEgp = 38.0,
            bonusEgp = 8.0,
            estimatedEarningsEgp = 46.00,
            paymentMethod = PaymentMethod.WALLET,
            orderTotalEgp = 495.0,
            status = OrderStatus.DELIVERED,
            itemsSummary = "2 ساندوتش شاورما فراخ عائلي + 1 فتة شاورما مكس",
            itemsList = listOf(
                OrderItem("i1", "ساندوتش شاورما فراخ عربي عائلي", "Arabic Chicken Shawarma Box", 2, 145.0),
                OrderItem("i3", "وجبة فتة شاورما مكس الريم", "Mixed Shawarma Fattah", 1, 180.0)
            ),
            specialInstructions = "تسليم باليد",
            createdAtFormatted = "اليوم، 11:45 ص"
        ),
        DeliveryTask(
            orderId = "ord_110",
            orderNumber = "DRV-8930",
            restaurantId = "rest_1",
            restaurantNameAr = "شاورما DROVA 6 أكتوبر",
            restaurantNameEn = "Al Reem Shawarma",
            restaurantAddressAr = "محور 26 يوليو، 6 أكتوبر",
            restaurantPhone = "+201000000000",
            pickupDistanceKm = 2.0,
            customerName = "هاني خليل",
            customerPhone = "+201066778899",
            customerAddressAr = "زهراء 6 أكتوبر، شطر 7، عمارة 21، شقة 5",
            dropoffDistanceKm = 3.2,
            estimatedTimeMin = 22,
            baseEarningsEgp = 42.0,
            bonusEgp = 10.0,
            estimatedEarningsEgp = 52.00,
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            orderTotalEgp = 345.0,
            status = OrderStatus.DELIVERED,
            itemsSummary = "1 شاورما لحم دوبل + 1 نصف دجاجة شواية",
            itemsList = listOf(
                OrderItem("i2", "ساندوتش شاورما لحم بلدي دوبل", "Double Beef Shawarma", 1, 165.0),
                OrderItem("i4", "نصف دجاجة شواية مع أرز وبطاطس", "Half Grilled Chicken Meal", 1, 155.0)
            ),
            specialInstructions = "تم تحصيل 345.0 ج.م كاش",
            createdAtFormatted = "اليوم، 12:30 م"
        ),
        DeliveryTask(
            orderId = "ord_100",
            orderNumber = "DRV-8940",
            restaurantId = "rest_4",
            restaurantNameAr = "بيتزا زايد الإيطالية",
            restaurantNameEn = "Pizza & Capri",
            restaurantAddressAr = "الشيخ زايد، الجيزة",
            restaurantPhone = "+201022334455",
            pickupDistanceKm = 3.4,
            customerName = "أحمد مصطفى",
            customerPhone = "+201012345678",
            customerAddressAr = "عمارة 14، حدائق أكتوبر، الجيزة",
            dropoffDistanceKm = 4.5,
            estimatedTimeMin = 35,
            baseEarningsEgp = 50.0,
            bonusEgp = 12.0,
            estimatedEarningsEgp = 62.00,
            paymentMethod = PaymentMethod.CREDIT_CARD,
            orderTotalEgp = 305.0,
            status = OrderStatus.DELIVERED,
            itemsSummary = "1 بيتزا مارجريتا نابوليتانا + 1 تيراميسو",
            itemsList = listOf(
                OrderItem("i7", "بيتزا مارجريتا نابوليتانا كلاسيك", "Neapolitan Margherita", 1, 190.0),
                OrderItem("i8", "تيراميسو ماسكاربوني إيطالي", "Classic Italian Tiramisu", 1, 85.0)
            ),
            createdAtFormatted = "أمس، 08:45 م"
        )
    )

    val defaultEarnings = CaptainEarnings(
        todayDeliveriesCount = 8,
        todayNetEarningsEgp = 385.00,
        weekEarningsEgp = 2450.00,
        walletBalanceEgp = 1240.50,
        pendingPayoutEgp = 860.00,
        baseEarningsEgp = 275.00,
        bonusesEgp = 110.00,
        deductionsEgp = 0.00,
        acceptanceRatePercent = 98,
        onTimeDeliveryRatePercent = 99
    )

    val defaultShiftData = CaptainShiftData(
        isShiftActive = true,
        shiftStartFormatted = "09:00 ص",
        hoursWorked = 5.5,
        scheduledHours = 8.0,
        hourlyGuaranteedRateEgp = 50.0,
        shiftBaseEarningsEgp = 275.0,
        shiftDeliveriesBonusEgp = 110.0,
        totalShiftEarningsEgp = 385.0
    )

    val sampleTransactions = listOf(
        CaptainTransaction(
            id = "tx_01",
            titleAr = "عائد توصيل طلب",
            titleEn = "Delivery Earning",
            referenceOrderNumber = "DRV-8930",
            dateFormatted = "اليوم، 12:45 م",
            amountEgp = 52.00,
            isCredit = true,
            type = CaptainTransactionType.TRIP_EARNING
        ),
        CaptainTransaction(
            id = "tx_02",
            titleAr = "عائد توصيل طلب",
            titleEn = "Delivery Earning",
            referenceOrderNumber = "DRV-8938",
            dateFormatted = "اليوم، 12:05 م",
            amountEgp = 46.00,
            isCredit = true,
            type = CaptainTransactionType.TRIP_EARNING
        ),
        CaptainTransaction(
            id = "tx_03",
            titleAr = "مكافأة إنجاز 5 طلبات في وقت الذروة",
            titleEn = "Peak Hours Milestone Bonus",
            referenceOrderNumber = null,
            dateFormatted = "اليوم، 11:30 ص",
            amountEgp = 50.00,
            isCredit = true,
            type = CaptainTransactionType.BONUS
        ),
        CaptainTransaction(
            id = "tx_04",
            titleAr = "تحويل أرباح إلى حساب فودافون كاش",
            titleEn = "Payout Transfer to Vodafone Cash",
            referenceOrderNumber = null,
            dateFormatted = "أمس، 06:00 م",
            amountEgp = 600.00,
            isCredit = false,
            type = CaptainTransactionType.PAYOUT_WITHDRAWAL,
            statusAr = "تم التحويل بنجاح",
            statusEn = "Transferred Successfully"
        ),
        CaptainTransaction(
            id = "tx_05",
            titleAr = "عائد توصيل طلب",
            titleEn = "Delivery Earning",
            referenceOrderNumber = "DRV-8940",
            dateFormatted = "أمس، 09:15 م",
            amountEgp = 62.00,
            isCredit = true,
            type = CaptainTransactionType.TRIP_EARNING
        )
    )

    val sampleCaptainNotifications = listOf(
        CaptainNotification(
            id = "notif_1",
            titleAr = "طلب توصيل جديد متاح بالقرب منك!",
            titleEn = "New Delivery Request Near You!",
            messageAr = "مطعم كشري التحرير بحدائق أكتوبر - العائد المتوقع 42.50 ج.م",
            messageEn = "Koshary El Tahrir, Dokki - Estimated 42.50 EGP",
            timestampFormatted = "منذ دقيقتين",
            type = CaptainNotificationType.NEW_REQUEST,
            isRead = false
        ),
        CaptainNotification(
            id = "notif_2",
            titleAr = "تم إيداع أرباح الرحلة في محفظتك",
            titleEn = "Trip Earnings Credited",
            messageAr = "تمت إضافة 52.00 ج.م للطلب DRV-8930 في محفظة DROVA",
            messageEn = "52.00 EGP added for order DRV-8930",
            timestampFormatted = "اليوم، 12:45 م",
            type = CaptainNotificationType.EARNINGS_CREDITED,
            isRead = true
        ),
        CaptainNotification(
            id = "notif_3",
            titleAr = "الطلب جاهز للاستلام بالمطعم",
            titleEn = "Order Ready for Pickup",
            messageAr = "قام مطعم شاورما الريم بتجهيز وتغليف الطلب DRV-8938 بالكامل",
            messageEn = "Al Reem Shawarma packaged order DRV-8938",
            timestampFormatted = "اليوم، 11:35 ص",
            type = CaptainNotificationType.RESTAURANT_READY,
            isRead = true
        ),
        CaptainNotification(
            id = "notif_4",
            titleAr = "مبروك! حققت حافز الوردية اليوم",
            titleEn = "Congrats! Shift Bonus Achieved",
            messageAr = "حصلت على 50.00 ج.م حافز إضافي لالتزامك بنسبة قبول 98%",
            messageEn = "Received 50.00 EGP bonus for 98% acceptance rate",
            timestampFormatted = "اليوم، 11:30 ص",
            type = CaptainNotificationType.EARNINGS_CREDITED,
            isRead = true
        )
    )
}
