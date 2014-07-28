SnapShop
================

The idea of this application is to make the entire ecommerce experience image centric. Take a photo of a product you want to buy or sell and let SnapShop do the rest of the work. SnapShop identifies the image and extracts the relevant keywords (Through the Camfind API) and then redirects the user to a page with 2 tabs, "SnapBuy" and "SnapSell". In the "SnapBuy" Tab the user sees relevant items on eBay similar to the product whose photo he took. In the "SnapSell" tab the user sees the List Item form on eBay autopopulated with details relevant to the product. The user can then enter other details and submit to List it on eBay. So with this, the entire buying and selling process becomes as easy as taking a picture! 

This is an android application and works best in Android 4.4.4. 

How to Use?

1. Import the application onto Android SDK
2. Set your CamFind API Key in resources/config.properties (You can get it by creating a default application in http://www.mashape.com/imagesearcher/camfind and selecting "Get Keys").
3. Install android app on your phone.
4. Take an image (or import from gallery). 
5. Click SnapSearch redirects you to the results page with 2 tabs for buying and selling the product.

