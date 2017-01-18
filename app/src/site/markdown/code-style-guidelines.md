## Guidelines

### Class name guidelines

* Dagger services should have a "Service" or "ServiceImpl" suffix.
* Android services, in order to avoid confusion between dagger services, should have a suffix of the class they're extending.
For example, a notification class extending "JobService" should be named "NotificationJobService".

### Misc.

* avoid creating styles objects, create style objects only for styles that are used in many places for the same purpose
* avoid creating many ids on elements, there is a limit from android for the number of ids each app can create.
  you can use the same id in different layouts for example "title" id for title element at offering page and at
  request page(assuming you do not need to get the title outside the layout), android will count it as 1 id.

* when sending intent the intent should be created by the activity you are sending to..
example for moving from MainActivity to CategoryPageActivity:

Bad:
MainActivity code:
>        Intent intent = new Intent(context, CategoryPageActivity.class);
>        intent.putExtra("offeringsByCategoryResult", offeringsByCategoryResult);
>        return intent;
no code at CategoryPageActivity

Good:
MainActivity code
>                Intent intent = CategoryPageActivity.createIntent(getApplicationContext(), offeringsByCategoryResult);
>                startActivity(intent);
CategoryPageActivity code:
>    public static Intent createIntent(Context context, MGOfferingsByCatalogGroupResult offeringsByCategoryResult) {
>        Intent intent = new Intent(context, CategoryPageActivity.class);
>        intent.putExtra(OFFERINGS_BY_CATEGORY_RESULT, offeringsByCategoryResult);
>        return intent;
>    }

* prefer using RecyclerView instead of ListView and try to use the RecyclerView power of not inflating every time a new item is added to the list