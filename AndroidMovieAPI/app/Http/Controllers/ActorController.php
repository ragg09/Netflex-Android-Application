<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use Illuminate\Support\Facades\Response;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Auth;
use App\Models\User;
use App\Models\Actor;
use App\Models\Film;
use App\Models\Producer;
use DB;
use File;
use Validator;

class ActorController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $data = Actor::all();
        return json_encode(['data'=>$data]);
        //return Response::json($data,200);
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {

        $Actor = new Actor;
        $Actor->name = $request->name;
        $Actor->note = $request->note;
        $files = $request->uploads;
        $Actor->image = 'storage/images/'.$request->name.'.jpg';
        $Actor->save();
        $data=array('status' => 'saved');
        //$data = $Actor->id;//getting id of newly added actor
        Storage::put('public/images/'.$request->name.'.jpg',base64_decode($request->uploads));
        return Response::json($data,200);

    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $data_actor = Actor::find($id);

        $data_movies = DB::table('film_actor')->where('actor_id', $id)->get("film_id"); 

        if(count($data_movies)>0){

            foreach ($data_movies as $key) {
                $movies[] = Film::find($key->film_id);
                // $movies[] = $key->film_id;

                $data_producer = DB::table('film_producer')->where('film_id', $key->film_id)->get('producer_id');
                foreach ($data_producer as $prod) {
                    $producers[] = Producer::find($prod->producer_id);
                }
            }

            return json_encode(['data_actor'=>$data_actor, 'movies' => $movies, "producers" => $producers, "count" => count($data_movies)]);
             // 'actor_cnt' => array('count'=>count($data_actors))

        }else{
             return json_encode(['data_actor'=>$data_actor, "count" => count($data_movies)]);
        }

        //return Response::json($data,200);
        
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        if($request->uploads == ""){
            $Actor = Actor::findOrFail($id);
            $Actor->update($request->all());
            $data=array('status' => 'saved');  
        }else{
            $Actor = Actor::findOrFail($id);
            $files = $request->uploads;
            $img_path = $request->name.'.jpg';
            $request['image'] = 'storage/images/'.$img_path;
            $Actor->update($request->all());
            $data=array('status' => 'saved');  
            Storage::put('public/images/'.$img_path,base64_decode($request->uploads));
        }    

       return Response::json($data,200);
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        DB::table('film_actor')->where('actor_id', $id)->delete();
        $Actor = Actor::findOrFail($id);
        $Actor->delete();
        $data=array('status' => 'deleted');
        return Response::json($data,200);
    }
}
