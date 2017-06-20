package utils;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bunny on 12/06/17.
 */

public class FireBaseHandler {
    private FirebaseDatabase mDatabase;


    public FireBaseHandler() {
        //hello he
        mDatabase = FirebaseDatabase.getInstance();

    }

    public void uploadOrder() {


    }

    public void downloadOrder(String saloonUID , String orderID , final OnOrderDownloadListner onOrderDownloadListner ) {

        DatabaseReference mDatabaseRef = mDatabase.getReference().child("Orders/" + saloonUID+"/"+orderID);


        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String saloonName = dataSnapshot.child("saloonName").getValue(String.class);

                Order order = dataSnapshot.getValue(Order.class);
                if(order != null) {
                    order.setOrderID(dataSnapshot.getKey());
                }
                onOrderDownloadListner.onOrder(order);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

    }

    public void uploadOrderList() {

    }

    public void downloadOrderList(String saloonUID, int limitTo, final OnOrderListener onOrderListener) {

        DatabaseReference myRef = mDatabase.getReference().child("Orders/" + saloonUID);

        Query myref2 = myRef.limitToLast(limitTo);
        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Order> orderArrayList = new ArrayList<Order>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Order order = snapshot.getValue(Order.class);
                    order.setOrderID(snapshot.getKey());
                    orderArrayList.add(order);
                }

                onOrderListener.onOrderList(orderArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void downloadOrderList(String saloonUID, int limitTo, String lastOrderId, final OnOrderListener onOrderListener) {

        DatabaseReference myRef = mDatabase.getReference().child("Orders/" + saloonUID);

        Query myref2 = myRef.orderByKey().endAt(lastOrderId).limitToLast(limitTo);
        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Order> orderArrayList = new ArrayList<Order>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Order order = snapshot.getValue(Order.class);
                    order.setOrderID(snapshot.getKey());
                    orderArrayList.add(order);
                }

                orderArrayList.remove(orderArrayList.size() - 1);

                onOrderListener.onOrderList(orderArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void uploadSaloonInfo(String saloonUID, String saloonKeyValue, String value, final OnSaloonDownload onSaloonDownload) {

        mDatabase.getReference().child("saloon/" + saloonUID + "/" + saloonKeyValue).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                onSaloonDownload.onSaloonValueUploaded(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                onSaloonDownload.onSaloonValueUploaded(false);
            }
        });


    }

    public void uploadSaloon(String saloonUID, Saloon saloon, final OnSaloonDownload onSaloonDownload) {

        mDatabase.getReference().child("saloon/" + saloonUID).setValue(saloon).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                onSaloonDownload.onSaloonValueUploaded(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                onSaloonDownload.onSaloonValueUploaded(false);
            }
        });


    }

    public void isRegisteredSaloon(String saloonUID, final OnSaloonInfoCheckListner onSaloonInfoCheckListner) {
        DatabaseReference myRef = mDatabase.getReference().child("saloon/" + saloonUID);

        //Query myref2 = myRef.orderByChild("saloonUID").equalTo(saloonUID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Saloon saloon = dataSnapshot.getValue(Saloon.class);

                if (saloon.getSaloonName() != null) {
                    if (!saloon.getSaloonName().isEmpty()) {


                        onSaloonInfoCheckListner.onIsRegistered(true, saloon.getSaloonName());

                    } else {

                        onSaloonInfoCheckListner.onIsRegistered(false, "");

                    }

                } else {
                    onSaloonInfoCheckListner.onIsRegistered(false, "");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                onSaloonInfoCheckListner.onIsRegistered(false, "");

            }
        });


    }

    public void downloadSaloon(String saloonUID, final OnSaloonDownload onSaloonDownload) {

        DatabaseReference myRef = mDatabase.getReference().child("saloon/" + saloonUID);


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String saloonName = dataSnapshot.child("saloonName").getValue(String.class);

                Saloon saloon = dataSnapshot.getValue(Saloon.class);
                saloon.setSaloonUID(dataSnapshot.getKey());

                onSaloonDownload.onSaloon(saloon);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }

    public void uploadSaloonShwcaseImage(Uri imageUri, String saloonUID, String imageName, final OnSaloonImageListner onSaloonImageListner) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        StorageReference riversRef = storageRef.child("saloon_image/" + saloonUID + "/" + imageName);
        UploadTask uploadTask = riversRef.putFile(imageUri);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                onSaloonImageListner.onImageUploaded(false, null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                onSaloonImageListner.onImageUploaded(true, downloadUrl);
            }
        });

    }

    public void downloadSaloonShwcaseImage(String saloonUID, String imageName, final OnSaloonImageListner onSaloonImageListner) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference islandRef = storageRef.child("saloon_image/" + saloonUID + "/" + imageName);
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (Exception exception) {

        }

        final File finalLocalFile = localFile;
        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                onSaloonImageListner.onImageDownLoaded(true, finalLocalFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                onSaloonImageListner.onImageDownLoaded(false, null);

            }
        });

    }

    public void uploadService(Service service, final OnServiceListener onServiceListener) {


        mDatabase.getReference().child("services/").push().setValue(service).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                onServiceListener.onSeviceUpload(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                onServiceListener.onSeviceUpload(false);
            }
        });

    }

    public void updateOrderstatus(String saloonUID, String orderId, final int orderStatus, final OnOrderStatusUpdateListener onOrderStatusUpdate) {

        DatabaseReference ref = mDatabase.getReference();


// Create the data we want to update
        Map post = new HashMap();
        post.put("Orders/" + saloonUID + "/" + orderId + "/" + "orderStatus", orderStatus);



// Do a deep-path update
        ref.updateChildren(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onOrderStatusUpdate.onOrderStatusUpdate(orderStatus, true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                onOrderStatusUpdate.onOrderStatusUpdate(0 , false);

            }
        });
    }

    public void updateOrderstatus(String saloonUID, String orderId, final int orderStatus,int saloonPoint , final OnOrderStatusUpdateListener onOrderStatusUpdate) {

        DatabaseReference ref = mDatabase.getReference();


// Create the data we want to update
        Map post = new HashMap();
        post.put("Orders/" + saloonUID + "/" + orderId + "/" + "orderStatus", orderStatus);
        post.put("saloon/" + saloonUID+"/"+"saloonPoint", saloonPoint+1);




// Do a deep-path update
        ref.updateChildren(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onOrderStatusUpdate.onOrderStatusUpdate(orderStatus, true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                onOrderStatusUpdate.onOrderStatusUpdate(0 , false);

            }
        });
    }


    public void downloadServiceList(String saloonUID , int limitTo  , final OnServiceListener onServiceListener){

        DatabaseReference myRef = mDatabase.getReference().child("services/");

        Query myref2 = myRef.orderByChild("saloonUID").equalTo(saloonUID).limitToLast(limitTo);
        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Service> serviceArrayList = new ArrayList<Service>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Service service = snapshot.getValue(Service.class);
                    if(service != null) {
                        service.setServiceUID(snapshot.getKey());
                    }
                    serviceArrayList.add(service);
                }


                onServiceListener.onServiceList(serviceArrayList,true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onServiceListener.onServiceList(null,false);

            }
        });

    }


    public interface OnSaloonInfoCheckListner {

        public void onIsRegistered(boolean isRegistered, String saloonName);

        public void onIsSaloonUpdated();

    }

    public interface OnSaloonDownload {
        public void onSaloon(Saloon saloon);

        public void onSaloonValueUploaded(boolean isSucessful);


    }


    public interface OnSaloonImageListner {
        public void onImageDownLoaded(boolean isSucessful, File imageFile);

        public void onImageUploaded(boolean isSucessful, Uri downloadUri);

    }

    public interface OnServiceListener {
        public void onSeviceUpload(boolean isSuccesful);

        public void onServiceList(ArrayList<Service> serviceArrayList , boolean isSuccesful);
    }

    public interface OnOrderListener {
        public void onOrderList(ArrayList<Order> orderArrayList);
    }

    public interface OnOrderStatusUpdateListener {
        public void onOrderStatusUpdate(int newStatus, boolean isSuccesful);
    }

    public interface OnOrderDownloadListner {
        public void onOrder(Order order);


    }





}

