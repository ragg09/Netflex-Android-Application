<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Film_actor extends Model
{
    use HasFactory;
    protected $table = 'film_actor';
    protected $fillable = ['film_id','actor_id'];
    public $timestamps = FALSE;
}
